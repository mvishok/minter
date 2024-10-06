# Check if the compiled class exists using a relative path
if (!(Test-Path -Path ".\bin\minter.class")) {
    Write-Host "Minter is not built. Please use the build command."
    exit
}

# Ensure an argument (filepath) is passed
if ($args.Count -eq 0) {
    Write-Host "No file provided. Please specify a .minter file to run."
    exit
}

# Get the .minter file path from the arguments
$minterFile = $args[0]

# Check if the provided .minter file exists
if (!(Test-Path -Path $minterFile)) {
    Write-Host "The specified file '$minterFile' does not exist."
    exit
}

# Run the Java command with relative paths and the specified .minter file
& "java.exe" --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp ".\lib\*;.\bin" minter $minterFile
