# NOTICE

BDD-Security is currently undergoing a refresh and a switch to OWASP ZAP.  The transition should be done by the end of November 2013.  The master git branch now points to the new ZAP version, and the current documentation is out of date.  Please check back soon for updated docs and code.


# Overview

BDD-Security is a framework written in Java and based on JBehave and Selenium 2 (WebDriver) that uses predefined security tests and an integrated security scanner to perform automated security assessments of web applications.

# Introduction
Detailed introduction and rationale: http://www.continuumsecurity.net/bdd-intro.html


# Release
Version: 0.2 

There are likely to be changes to the core and API as it is tested on a wider variety of web applications.

# Features
- Predefined security stories for:
	- Authentication
	- Session Management
	- Authorisation
	- Automated scanning through Burp Suite

# Install

## Install captchaswerve

It's a CAPTCHA solving library that uses the CaptchaBuster online service.  If you don't need CAPTCHA solving, then the classes that depend on it will have to be removed.
https://github.com/stephendv/captchaswerve	

## Install Resty-Burp
Follow the installation instructions for resty-burp: https://github.com/stephendv/resty-burp#readme

Start the service (which will start Burp)

	mvn exec:java

## Setup BDD-Security
	git clone git@github.com:stephendv/bdd-security.git bdd-hacmebooks
### compile
	mvn compile
### Create Eclipse project
	mvn eclipse:eclipse

From eclipse, import existing project into workspace.

Install the JBehave Eclipse plugin for syntax highlighting of story text: https://github.com/Arnauld/jbehave-eclipse-plugin

## Run
Useful to create a run configuration that runs the StoryRunner class and use ${string_prompt} for the program arguments so that you're prompted for args.

## Arguments
- -c 	Only run configuration_story.story
- -s	Skip configuration_story.story
- -m	Use JBehave meta tags, examples: 
	- 	-m "+id auth_case"   to just run the Scenario tagged with @id auth_case
	-	-m "+story Authentication"	to just run the Authentication story
	-	-m "-story Automated Scanning" run all stories except Automated Scanning
