param(
    [Parameter(Mandatory = $true)]
    [string]$DockerHubUser,

    [string]$Tag = "latest"
)

$ErrorActionPreference = "Stop"
$Image = "${DockerHubUser}/psymed-backend:${Tag}"

Write-Host "Building $Image ..." -ForegroundColor Cyan
docker build -t $Image .

Write-Host "Pushing $Image ..." -ForegroundColor Cyan
docker push $Image

Write-Host "Done. Image published: $Image" -ForegroundColor Green
