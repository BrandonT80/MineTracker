node {
  stage('SCM') {
    checkout scm
  }
  stage('SonarQube Analysis') {
    def mvn = tool 'FableMaven';
    withSonarQubeEnv() {
      bat "${mvn}/bin/mvn clean verify sonar:sonar -Dsonar.projectKey=BrandonT80_MineTracker_9ffcae7a-3ec7-40e6-bd1b-efb7ddd5a310 -Dsonar.projectName='MineTracker'"
    }
  }
}