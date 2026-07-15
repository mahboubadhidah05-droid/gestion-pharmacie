pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    stages {

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }


        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }


        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }


        stage('Deploy to Nexus') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-creds',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {

                    sh '''
                        cat > settings-nexus.xml << EOF
<settings>
  <servers>
    <server>
      <id>nexus-releases</id>
      <username>${NEXUS_USER}</username>
      <password>${NEXUS_PASS}</password>
    </server>

    <server>
      <id>nexus-snapshots</id>
      <username>${NEXUS_USER}</username>
      <password>${NEXUS_PASS}</password>
    </server>
  </servers>
</settings>
EOF

                        mvn deploy -DskipTests -s settings-nexus.xml
                    '''
                }
            }
        }


        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                    -t pharmacie-app:latest .
                '''
            }
        }


     stage('Deploy Application') {
    steps {
        sh '''
            docker rm -f pharmacie-app || true

            docker run -d \
            --name pharmacie-app \
            --network pharmacie-net \
            -e DB_URL="jdbc:mysql://mysql-container:3306/pharmacie" \
            -e DB_USER="root" \
            -e DB_PASSWORD="douaa" \
            pharmacie-app:latest
        '''
    }
}


        stage('Verify Deployment') {
            steps {
                sh '''
                    docker ps
                    docker logs --tail 50 pharmacie-app
                '''
            }
        }
    }
}