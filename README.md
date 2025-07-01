# Maximum Futility
by Mark Abersold

## What is this project?
This is the result of a question I asked myself one day: "What is the most hopeless sports town in North America?" I decided to gather the data together and calculate it.

## Requirements

Java 17 - if you are using IntelliJ, check under Settings -> Build, Execution & Deployment -> Build Tools -> Gradle. Check Gradle JVM to make sure you have a compatible Java SDK selected.  

## Running the Application

Please note, the first time you run the application, you will need to generate and seed the database.

### From IntelliJ IDEA

Open `src/main/kotlin/mabersold/Application.kt`. Just click the "run" button next to `main`. Be sure to go to your run configurations and save the new configuration that was just generated.

### From the command line
This is built in Kotlin using the KTor framework. You can build it and run it using gradle.

    gradle build
    gradle run

Or, if you don't have gradle installed, using the handy gradlew files that are included.

    gradlew build
    gradlew run

Once built, direct your browser to http://localhost:8080 to see the output.

### Generating the Database Schema

You will need to generate the database schema with a command line argument. You only need to use this the first time you run the application, or if you ever want to reset the database.

Run with the command line argument `--populate` to create the database schema and seed it with data. This will create an H2 database file called `maximum_futility.db` in the build directory.

    gradle run --args="--populate"

If you are running in IntelliJ IDEA, edit your run configuration and add `--populate` to your program arguments.

This will create the schema and seed it with data. If you want, you can create the schema without seeding it by using the `--createDB` option instead.

After creating the schema, you can omit the command line argument in future runs.

## How Maximum Futility Works

The data is stored in an H2 database. Although the main output is by city, the data is stored by franchise. When you load the main page, it maps the franchise data to city data. It also performs all the necessary calculations to give you the output.

### Where is the data?
Look under src/main/resources/data for the CSV files that are used to populate the database.

### What if I see a mistake in your data?
Please file an issue to correct it! Or, if you're feeling confident, you can make the change yourself and open a pull request.