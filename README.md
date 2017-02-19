# MadHacks2k17

Project for Madhacks Hackathon 2017

Team Members: Jenny Day, Jon Doenier, Emily Hinds, Lili Kim

Title: Reverse Pacman

Explanation: Our hack allows the player to play as a ghost within Pacman, fighting off
multiple "PacMen" as they eat the dots on the board. With each dot consumed by the PacMen,
the player's score decreases; the goal is to defeat all of the PacMen before they eat
the whole board. The player wins once they beat the level with the maximum number of PacMen
allowed within the code

Methods: We took an open-source Pacman version of the code (from 
http://zetcode.com/tutorials/javagamestutorial/pacman/), made it object-oriented 
(by creating Ghost and PacMen classes), and reversed the logic for the Pacman and ghost movement.
Images were created based on popular PacMan logos. In addition, we began to implement 
a board randomization element by splitting the board into three zones: each zone 
has a number of maze elements that can be chosen for a large number of possible combinations. 

Purpose: We wanted to learn basic 2D game development and graphics. 

Future possibilities: We haven't fully implemented the board randomization (right now
it just replicates the hardcoded maze found in the base code). We can also add powerups
(increased speed, allied ghosts, extra points), and make the PacMen smarter (currently their movement
is random). Other ideas: customizable ghost color, customizable maximum number of Pacmen
(from the command line).
Unrelated to our submission, we plan to actually learn how to use GitHub. All of our collaboration
was done by uploading to Google Docs because we wasted too much time struggling with GitHub
and decided to just focus on the game.
