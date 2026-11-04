if (-not $env:JAVA_HOME) {
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12.1"
}
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
mvn @args

