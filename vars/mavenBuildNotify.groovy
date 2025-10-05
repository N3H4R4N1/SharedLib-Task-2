def call(Map config = [:]) {
    def repoUrl = config.repoUrl ?: 'https://github.com/example/project.git'
    def branch = config.branch ?: 'main'
    def mavenCmd = config.mavenCmd ?: 'clean package'
    def repoName = repoUrl.tokenize('/').last().replace('.git', '')

    stage('Clone Repository') {
        echo "Cloning repository: ${repoUrl} (branch: ${branch})"
        dir(repoName) {
            checkout([$class: 'GitSCM',
                branches: [[name: "*/${branch}"]],
                userRemoteConfigs: [[url: repoUrl]]
            ])
        }
    }

    def buildStatus = "SUCCESS"

    stage('Build with Maven') {
        try {
            dir(repoName) { // ✅ Run inside the repo folder
                echo "Running Maven command: mvn ${mavenCmd}"
                sh "mvn ${mavenCmd}"
            }
        } catch (err) {
            buildStatus = "FAILED"
            error "Build failed with error: ${err}"
        }
    }

    stage('Send Notification') {
        def notifier = new org.example.Notification()
        notifier.sendMessage(repoUrl, branch, mavenCmd, buildStatus)
    }
}
