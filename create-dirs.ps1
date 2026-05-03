$basePath = "C:\Users\Administrator\AndroidStudioProjects\Grocerystore\grocery-backend"
$dirs = @(
    "src\main\java\com\example\grocerystore\config",
    "src\main\java\com\example\grocerystore\controller",
    "src\main\java\com\example\grocerystore\service",
    "src\main\java\com\example\grocerystore\repository",
    "src\main\java\com\example\grocerystore\entity",
    "src\main\java\com\example\grocerystore\dto\request",
    "src\main\java\com\example\grocerystore\dto\response",
    "src\main\java\com\example\grocerystore\security",
    "src\main\java\com\example\grocerystore\exception",
    "src\main\resources",
    "src\test\java\com\example\grocerystore"
)

foreach ($dir in $dirs) {
    $fullPath = Join-Path $basePath $dir
    New-Item -ItemType Directory -Force -Path $fullPath | Out-Null
    Write-Host "Created: $fullPath"
}

Write-Host "All directories created successfully!"
