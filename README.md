# Mimir-Event-Interface
Part of my Mimir project which store schemas, generate and provide objects and commons.

## Mimir Goals

Mimir is a personal training project regrouping little software bricks aimed at facilitating inter-system data communication, a kind of message oriented data hub.
As Data Engineer, I have also a specific case in mind : Data ingestions from several systems to a "centralized" data storage for analytics and reporting purpose.

## Schema compatibility

If Message have more fields than POJO, it will raise some warning in the logs.

If POJO have more fields than Message, POJO fields not in Message should NULL, so either we can check and raise alert if some fields are null, or considering those fields optionnal we could check DATA quality part later in the process (actually we could compute some Data Quality check here).

## How to prevent not wished schema message to be pushed in a topic

Well we can:
- define a standard defining that schema name is the prefix topic name
- create producer (and consumer) directly in the maven project to limit configuration interaction
- create dedicated credentials to only push in this specific topic

TODO:
- Modify the repositories in .mvn\settings.xml used in .github\workflows\build-n-deploy.yml to point on your own maven repository
- Add your credentials as repo secrets and use them as ARTIFACTORY_USER and ARTIFACTORY_PWD are used.
- Add CI_USER and CI_PWD as repo secrets in order to update documentation from the CI. They are credentials with WRITE permission to your repo.