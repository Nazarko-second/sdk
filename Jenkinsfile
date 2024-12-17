pipeline {
    agent any
    stages {
        stage('Verilife Desktop Regression') {
            steps {
                catchError {
                    browserstack(credentialsId: 'e95a8d84-1a4d-4488-b561-27d79ebaa786') {
                        sh 'export BROWSERSTACK_BUILD_NAME=$( date "+%F_%H:%M:%S" ); mvn install -X -Dconfig=stage_chrome -Dgroups=ageGate.OpenAppURLTest -DexcludedGroups=mobileOnly -DqTest=false "-Dos=Windows" -Dbrowser=Chrome -DbuildUrl=${BUILD_URL} -DfullDataProviderDetails=false "-Dbrowserstack.config=browserstack.yml"'
                    }
                }
            }
        }
//         stage('Verilife Mobile Regression') {
//             steps {
//                 browserstack(credentialsId: 'e95a8d84-1a4d-4488-b561-27d79ebaa786') {
//                    catchError {
//                        sh 'export BROWSERSTACK_BUILD_NAME=$( date "+%F_%H:%M:%S" ); mvn install -Dconfig=test_illinois_android_chrome_bs -Dgroups=myFavorites.VerifyMyFavoritesPageMobileTest -DexcludedGroups=desktopOnly -DqTest=false "-Dos=Windows" -Dbrowser=Chrome -DbuildUrl=${BUILD_URL} -DfullDataProviderDetails=false -Dbrowserstack.config="android.yml"'
//                    }
//                 }
//             }
//         }
    }
}