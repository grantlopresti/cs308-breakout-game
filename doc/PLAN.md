# Game Plan
## Grant LoPresti (gjl13)


### Breakout Variant
I found the breakout variant "Bricks n' Balls" to be very interesting as it added a strategy component to the game. 
This was done by adding a "point and shoot" mechanism where the user could precisely determine how to aim the ball.
The game shown however didn't incorporate the traditional paddle which is needed to prevent the balls from hitting the bottom of the screen.
I was disappointed this element was omitted, but also really liked how each block in the game had to be hit a certain number of times before it broke.

I will be incorporating the "aim to shoot" and "multiple hits to break" elements of the Bricks n' Balls game into a more traditional version of Breakout.
The specifics of this implementation are described in the "Something Extra" Section below.

### General Level Descriptions
There will be 4 pages/screens to the game:
1. A welcome "splash screen" which will also serve as an end game screen
    - The screen will show the game's name and a button to begin at level 1 or choose levels 2 or 3.
Levels 2 and 3 will be "grayed out" in some way until the user beats levels 1 and 2 respectively (think checkpoints)
    - There will also be a message on the screen that defaults to "Welcome!", but can also be "Try Again!", or "You Won!" depending on the circumstances
2. Level One will be the first and easiest of those the player will encounter
    - The blocks will all be stationary and laid out in 3 parallel rows with an 80:10:10 ratio of Single:Double:Triple hit blocks
    - The player's paddle and ball will move at reasonable speeds "1" relative to the other levels 
3. Level Two will be the second level the player will encounter
    - There will be 4 layers of stationary blocks laid out in an upper and lower set of 2 parallel rows with a 60:25:15 ratio of Single:Double:Triple hit blocks
    - There will be 2 layers in-between with a moving block moving at a reasonable speed
    - The player's paddle and ball will move at speeds "0.8" and "1.2" respectively, relative to the other levels 
4. Level Three will be the last and most difficult of those the player will encounter
    - There will be 5 layers of stationary blocks laid out in rows with a 40:35:25 ratio of Single:Double:Triple hit blocks
    - In-between each stationary layer will be a row with a moving block moving at a slightly faster speed (4 total)
    - The player's paddle and ball will move at speeds "0.65" and "1.35" respectively, relative to the other levels 

### Bricks Ideas
There will be 4 types of blocks included in the game:
1. One hit blocks. Yellow, takes a single hit to break, gives 100 points when broken.
2. Two Hit Blocks. Orange, takes two hits to break, gives 250 points when broken.
3. Three hit Blocks. Green, takes three hits to break, gives 500 points when broken.
4. Moving blocks. Some colorful design, take up an entire row to themselves (normal size, but no blocks next to them) and take 2 hits to break.
They bounce back and forth between each side of the game's boundaries and give 1,000 points when broken.

### Power Up Ideas
The following ideas are standard and incorporated into the entire game play
 - Breaking one of the "Moving" blocks in Level 2 or 3 will slow down the remaining "Moving" blocks to half speed for approximately 15 seconds
 - Breaking an entire row of stationary blocks will grant the player an extra life (+1)
 
The remaining ideas randomly occur with a 10% chance and are activated for approx 5 seconds when they are 
 - Paddle Size Adjustment: Randomly assigns either 66% or 150% paddle size (width)
 - Ball Speed Adjustment: Randomly assigns either 66% or 150% ball speed
 - Damage Level Adjustment: Randomly assigns either double or zero damage done by the ball
 
### Cheat Key Ideas
 - The 'Q' key will QUIT the game and return you to the splash screen
 - The 'A' key will cause the ball to stick to the paddle the next time it collides and will allow it to then be AIMED
 - The 'U' key will aim the ball precisely UPWARDS
 - The 'L' key will add additional lives to the player
 - The 'R' key will reset the ball and paddle to their starting position
 - The '1-3' keys will clear the current level and jump to the level corresponding to the number pressed
 
### Something Extra
To add an element of skill to the game. Every 10ish successful "bounces" with the paddle will trigger the special "aiming" function.
The paddle will still be controlled left and right with the arrow keys in the traditional game, but will be frozen in this moment.
The arrow keys will the be used to "aim" the ball in a specific direction and then the 'space' key to launch it.
After this, the game will resume like normal until approximately 10 more bounces occur.
