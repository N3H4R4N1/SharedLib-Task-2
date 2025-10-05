package org.example

class Notification implements Serializable {

    def sendMessage(String repo, String branch, String command, String status) {
        println "--------------------------------------"
        println "🔔 Build Notification"
        println "Repository : ${repo}"
        println "Branch     : ${branch}"
        println "Command    : mvn ${command}"
        println "Status     : ${status}"
        println "--------------------------------------"

        // For Slack or Telegram, you can add API calls here.
        // Example: sh "curl -X POST -H 'Content-type: application/json' --data '{\"text\":\"Build ${status} for ${repo}\"}' ${SLACK_WEBHOOK_URL}"
    }
}
