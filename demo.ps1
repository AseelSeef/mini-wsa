# Prerequisites: Mini WSA running and events.json generated.
# Re-running demonstrates duplicate handling.

param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$InputFile = "$PSScriptRoot/events.json",
    [int]$BatchSize = 100
)

$ErrorActionPreference = "Stop"
$BaseUrl = $BaseUrl.TrimEnd("/")

if ($BatchSize -lt 1) {
    throw "BatchSize must be positive."
}

if (-not (Test-Path -LiteralPath $InputFile)) {
    throw "Input file not found. Run SecurityEventGenerator first."
}

$events = Get-Content -Raw -Encoding UTF8 -LiteralPath $InputFile |
    ConvertFrom-Json

if ($events.Count -eq 0) {
    throw "Input file contains no events."
}

# 1. Ingest events in batches.
Write-Host "`n=== Ingestion ==="

$created = 0
$existing = 0
$rejected = 0

for ($i = 0; $i -lt $events.Count; $i += $BatchSize) {
    $last = [Math]::Min($i + $BatchSize - 1, $events.Count - 1)
    $batch = @($events[$i..$last])
    $body = ConvertTo-Json -InputObject $batch -Depth 10 -Compress

    try {
        $response = Invoke-RestMethod `
            -Uri "$BaseUrl/v1/events/ingest" `
            -Method Post `
            -ContentType "application/json; charset=utf-8" `
            -Body ([System.Text.Encoding]::UTF8.GetBytes($body))
    }
    catch {
        Write-Host "Failed batch starting at index $i"
        Write-Host $_.ErrorDetails.Message
        throw
    }

    $created += $response.created
    $existing += $response.existing
    $rejected += $response.rejected

    Write-Host "Processed $($last + 1) / $($events.Count)"
}

Write-Host "Created: $created | Existing: $existing | Rejected: $rejected"

# Derive the query range from the file, including its last event.
$times = @(
    $events | ForEach-Object {
        [DateTimeOffset]::Parse($_.timestamp)
    } | Sort-Object
)

$from = [Uri]::EscapeDataString($times[0].ToUniversalTime().ToString("o"))
$to = [Uri]::EscapeDataString(
    $times[-1].AddSeconds(1).ToUniversalTime().ToString("o")
)

# 2. Display summary statistics for the generated time range.
Write-Host "`n=== Summary ==="

Invoke-RestMethod `
    -Uri "$BaseUrl/v1/stats/summary?from=$from&to=$to" |
    ConvertTo-Json -Depth 10

# 3. Display three matching samples.
Write-Host "`n=== Samples ==="

Invoke-RestMethod `
    -Uri "$BaseUrl/v1/events/samples?from=$from&to=$to&limit=3&offset=0" |
    ConvertTo-Json -Depth 10

# 4. Display event counts in five-minute buckets.
Write-Host "`n=== Time Series ==="

Invoke-RestMethod `
    -Uri "$BaseUrl/v1/stats/timeseries?from=$from&to=$to&interval=5m" |
    ConvertTo-Json -Depth 10