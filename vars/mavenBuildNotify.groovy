def call(Map config = [:]) {
    // Configurable parameters
    def repoUrl = config.repoUrl ?: 'https://github.com/example/project.git'
    def branch = config.branch ?: 'main'
    def mavenCmd = config.mavenCmd ?: 'clean package'

    stage('Clone Repository') {
        echo "Cloning repository: ${repoUrl} (branch: ${branch})"
        checkout([$class: 'GitSCM',
            branches: [[name: "*/${branch}"]],
            userRemoteConfigs: [[url: repoUrl]]
        ])
    }

    def buildStatus = "SUCCESS"

    stage('Build with Maven') {
        try {
            echo "Running Maven command: mvn ${mavenCmd}"
            sh "mvn ${mavenCmd}"
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
