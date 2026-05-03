$basePath = "C:\Users\Administrator\AndroidStudioProjects\Grocerystore\app\src\main\java\com\example\grocerystore"
$dirs = @(
    "api",
    "repository"
)

foreach ($dir in $dirs) {
    $fullPath = Join-Path $basePath $dir
    New-Item -ItemType Directory -Force -Path $fullPath | Out-Null
    Write-Host "Created: $fullPath"
}

Write-Host "Android API directories created successfully!"
