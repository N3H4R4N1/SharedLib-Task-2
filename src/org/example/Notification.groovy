package org.example

class Notification implements Serializable {

    def steps

    Notification(steps) {
        this.steps = steps
    }

    def sendMessage(String repo, String branch, String command, String status, String emailId) {
        def subject = "🔔 Jenkins Build ${status}: ${repo}"
        def body = """
        <h2>Jenkins Build Notification</h2>
        <p><b>Repository:</b> ${repo}</p>
        <p><b>Branch:</b> ${branch}</p>
        <p><b>Command:</b> mvn ${command}</p>
        <p><b>Status:</b> ${status}</p>
        <hr>
        <p>Sent automatically by Jenkins Shared Library.</p>
        """

        steps.echo "📧 Sending email to ${emailId}..."
        steps.script {
            steps.emailext(
                subject: subject,
                body: body,
                to: emailId,
                mimeType: 'text/html'
            )
        }
    }
}
