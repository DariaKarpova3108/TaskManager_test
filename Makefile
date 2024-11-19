setup:
	./gradlew wrapper --gradle-version 8.7
	./gradlew build

app:
	./gradlew bootRun --args='--spring.profiles.active=dev'

clean:
	./gradlew clean

build:
	./gradlew clean build

dev: app

reload-classes:
	./gradlew -t classes

install:
	./gradlew installDist

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

check-java-deps:
	./gradlew dependencyUpdates -Drevision=release

.PHONY: build