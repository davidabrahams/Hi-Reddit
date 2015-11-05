#Hi Reddit
![enter image description here](https://lh3.googleusercontent.com/-9fvosbrxwos/VjvUE7ati4I/AAAAAAAAAIg/aWjkpVAVlBo/s0/reddit-logo-01-674x5011.jpg "reddit-logo-01-674x5011.jpg")

####David Abrahams, Yuzhong Huang, Nora Mohamed, Lucy Wilcox

##Project Description:
	
Our project is a Siri app that responds with Reddit comments. This app is for people who read Reddit and want to get Reddit comments read aloud to them at their voice command. The vast majority of Reddit users rarely or never post to Reddit and this gives them a way to receive snarky Reddit responses via voice. Additionally, many people only use Siri (or OK Google) for comedic purposes -- they say funny things to Siri to get a funny response. By using Reddit and crowdsourcing our responses, our OK Google will be much funnier and snarkier than the original alternative.
	
##Project Goals:

 - We all wanted to learn about APIs which our app will allow us to do,
   as we will have to use a Reddit API and possibly others.
 - We want to implement a good user interface/user design, with advanced
   components.  Display live text when you’re saying it.
 - This interface will include real time graphic changes based on the
   user's verbal input. Have excellent unit tests.
 - Stretch goal: Opening up on “Hi-Reddit”.
 - Stretch goal: provide the ability to upvote or downvote Reddit
   comments after they are read aloud. To do this we would need the user
   to log into a Reddit account and use Reddit’s API.
 - Stretch goal: Be able to choose a subreddit or use their subscribed
   preferences.
 - Stretch goal: Be able to respond to comments.
 - Stretch goal TBD: ask people during initial user research what they
   want and implement that.

	
##Project Specifications:

Here are some technologies that we would use to create our app: 

 - An API that lets us search through Reddit comments. Or possibly using
   a database of comments.
    - jReddit
    - An API from Reddit
 - Do HTTP requests in order to search Reddit
    - Volley
 - Voice recognition library/SDK that turns speech to text, and also
   another that turns text  to speech
    - Voice capabilities
    - android.speech
 - We could possibly machine learning to better search for comments.This would probably include using a database, or we could use some of indico’s APIs to get the most relevantkeywords or tags that relate to our Reddit comment.
   
 - Possible use Google Voice’s search bar

##Project Deliverables and Timeline: 
###Checkin 1: November 17th 

 1. We have a basic UI built
 2. Perform user tests and get feedback
 3. We have voice recognition functioning
 4. The app should be able to speak (perhaps just saying back what you
    said)
 5. Be able to search reddit for what the user said.

##Checkin 2: December 1st

 1. Be able to search reddit for response.
 2. Have method created for figuring out what words we should or should
    not be searching for based on the user’s input.
 3. Improve algorithm for finding the best comment to read back (up
    votes, length, words used, parts of speech, sentiment etc.)

