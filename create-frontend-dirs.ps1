$basePath = "C:\Users\Administrator\AndroidStudioProjects\Grocerystore\grocery-frontend"
$dirs = @(
    "src\api",
    "src\components\layout",
    "src\components\product",
    "src\components\cart",
    "src\components\order",
    "src\components\common",
    "src\pages",
    "src\hooks",
    "src\stores",
    "src\i18n",
    "src\types",
    "src\utils",
    "public"
)

foreach ($dir in $dirs) {
    $fullPath = Join-Path $basePath $dir
    New-Item -ItemType Directory -Force -Path $fullPath | Out-Null
    Write-Host "Created: $fullPath"
}

Write-Host "Frontend directories created successfully!"
