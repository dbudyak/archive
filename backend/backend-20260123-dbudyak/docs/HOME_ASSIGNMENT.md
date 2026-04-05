# Overview

This is a coding assignment for a backend developer position.

Homework evaluation is one of the criteria we use when selecting the candidates for the interview, so pay attention that your solution demonstrates your skills in developing production quality code.

Please use Python for the exercise, otherwise, you have the freedom to select suitable tools and libraries (with a few exceptions listed below), but make sure the work demonstrates well your own coding skills.

The implementation is discussed as one topic during the technical interview.

You will be given access to a private repository on GitHub where you should push your solution; to return your homework, store the code and related documentation on such repository for easy access; then, notify your contact that you're done.

If you ran out of time and you are returning a partial solution, describe what is missing and how you would continue.

Your code will only be used for the evaluation, and you’re free to use it as you like, as you own it.

# Exercise

Your task is to implement a program that monitors the availability of many websites over the network, produces metrics about these and stores the metrics into a PostgreSQL database.

The website monitor should perform the checks periodically and collect the request timestamp, the response time, the HTTP status code, as well as optionally checking the returned page contents for a regex pattern that is expected to be found on the page. Each URL should be checked periodically, with the ability to configure the interval (between 5 and 300 seconds) and the regexp on a per-URL basis. The monitored URLs can be anything found online. In case the check fails the details of the failure should be logged into the database.

You may use any managed PostgreSQL service to speed you up while working on your assignment.

The solution MUST NOT include using any of the following:

* Database ORM libraries - use a Python DB API or similar library and raw SQL queries instead.
* External Scheduling libraries - we really want to see your take on concurrency.

# Criteria for evaluation

* Please keep the code simple and understandable. Anything unnecessarily complex, undocumented or untestable will be considered a minus.
* Main design goal is maintainability.
* The solution
  * Must work (we need to be able to run the solution)
  * Must be tested and have tests
  * Must handle errors.
  * Should be production quality.
  * Should work for at least some thousands of separate sites (no need to provide proof of this).
  * Note! If something is not implemented in a way that meets these requirements e.g. due to time constraints, explicitly state these shortcomings and explain what would be the correct way of implementing it.
* Code formatting and clarity: “Programs must be written for people to read, and only incidentally for machines to execute.” (Harold Abelson, Structure and Interpretation of Computer Programs)
* Attribution. If you take code from Google results, examples etc., add attributions. We all know new things are often written based on search results.
* Continuous Integration by itself or extensive container build recipes are not evaluated.
