# BlaBlaCovoit RT0809

### 1. Installation (macOS)
```bash
brew install openjdk@17 maven
```

### 2. Configuration Java 17
```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="$JAVA_HOME/bin:$PATH"
```

### 3. Lancer l'application
```bash
mvn clean wildfly:run
```

### 4. Accéder au site
Ouvrir dans le navigateur :  
**http://localhost:8080/covoiturage-1.0-SNAPSHOT/**
