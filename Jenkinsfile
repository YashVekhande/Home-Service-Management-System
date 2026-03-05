pipeline {
    agent any
    stages {
        stage('Build Docker Image') {
            steps {
                // Replace 'myapp' with your desired image name
                bat 'docker build -t myapp .' 
            }
        }
        stage('Run Container') {
            steps {
                // Maps container port 80 to host port 8080
                bat 'docker run -d -p 8081:80 myapp'
            }
        }
    }
}
