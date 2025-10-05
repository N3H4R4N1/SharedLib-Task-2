def call(Map config = [:]) {

    // ===== Configurable Inputs =====
    def repoUrl  = config.repoUrl  ?: 'https://github.com/example/project.git'
    def branch   = config.branch   ?: 'master'
    def mavenCmd = config.mavenCmd ?: 'clean package'
    def repoName = repoUrl.tokenize('/').last().replace('.git', '')
    def buildStatus = "SUCCESS"

    // ===== Stage 1 : Clone Repository =====
    stage('Clone Repository') {
        echo "🔄 Cloning repository: ${repoUrl} (branch: ${branch})"
        dir(repoName) {
            checkout([$class: 'GitSCM',
                branches: [[name: "*/${branch}"]],
                userRemoteConfigs: [[url: repoUrl]]
            ])
        }
    }

    // ===== Stage 2 : Build with Maven (JDK 11) =====
    stage('Build with Maven (Temurin 11)') {
        try {
            dir(repoName) {
                withEnv(["JAVA_HOME=/usr/lib/jvm/temurin-11-jdk-arm64",
                         "PATH=/usr/lib/jvm/temurin-11-jdk-arm64/bin:${env.PATH}"]) {
                    echo "⚙️ Using JAVA_HOME = ${env.JAVA_HOME}"
                    echo "⚙️ Running: mvn ${mavenCmd} with compiler plugin 3.8.1 and Java 11"
                    sh '''
                    mvn clean package \
                      org.apache.maven.plugins:maven-compiler-plugin:3.8.1:compile \
                      -Dmaven.compiler.source=11 \
                      -Dmaven.compiler.target=11 \
                      -Dmaven.compiler.release=11
                    '''
                }
            }
        } catch (err) {
            buildStatus = "FAILED"
            echo "❌ Build failed: ${err}"
            error("Stopping pipeline due to build failure.")
        }
    }

    // ===== Stage 3 : Archive Artifacts =====
    stage('Archive Artifact') {
        dir(repoName) {
            echo "📦 Archiving target/*.jar or *.war files..."
            archiveArtifacts artifacts: 'target/*.jar, target/*.war', fingerprint: true
        }
    }

    // ===== Stage 4 : Send Notification =====
    stage('Send Notification') {
        def notifier = new org.example.Notification()
        notifier.sendMessage(repoUrl, branch, mavenCmd, buildStatus)
    }
}
