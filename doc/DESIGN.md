Breakout Game Design
====
### Contributors

Grant LoPresti (gjl13) - Contributed everything to the project. Full design & implementation of all methods and classes.

### Project Design Goals

The original goals of this project were honestly to have a fully functional game where "extra features" were easy to
 implement and bugs were easy to find and fix. This meant having well designed classes and methods that could be
  easily understood and used to implement new features. As the project progressed I became more excited to add
   features to the blocks, for instance when I made the Moving blocks on levels 2 and 3. I realized that using
    inheritance was also a smart goal to have in my project. I wish that I would have had the opportunity to
     essentially re-create my entire project knowing now what I do about how things should have been initially
      designed. Needless to say, I've learned a lot.


### High Level Design

The Breakout project as originally submitted is mostly based off of the Main class which creates Bouncer, Brick, and
 Powerup objects. The Bouncer object is kept in a class of its own, while the Brick and Powerup objects are
  subclasses of a MovingObject class. Since finishing this project, I've really begun to understand the importance of
   inheritance and how it can be used to make dealing with the most broad/basic concepts, as well as the most niche
   /specific concepts, relatively easy. As is, the Main class creates MovingObjects of type Brick and Powerup (which
    are actually called in relation to their corresponding Brick) and then handles telling them do do certain things
    . I've learned that I should have transferred more of this "doing" power to the Moving Object and Brick/Powerup
     classes, for instance to have them all keep track of their own positions and boundary boxes so that main didn't
      have to deal with calling .getBoundsInParents() and similar. The bouncer on its own was simply controlled by
       the Main method as well (which I have since realized is far too much dependence on a single class).

### Assumptions & Decisions

I originally assumed that bricks would have specific images corresponding to their type (hit number and moving state
) when they were originally created. This made it quite difficult to do things like change the image to that of a one
 hit less block whenever they are hit with the balls. Because of my original assumption I wasn't able to implement
  features like a moving block that could visually display how many hits left (by changing its color/image) as the
   only way to do this in my code was to replace it with a type 1-hit block which didn't move for example. I also
    made the incorrect assumption that there was an inherent difference between all of my ImageView objects, when I
     should have simply made an abstract Sprite superclass that could have help helpful information concerning each
      and every object's position and attributes. I did make decisions however, such as creating a MovingObject class
       that both Brick and Powerup are a part of to do something similar to what a "sprite" class would have done.
       
### Addition of new features

describe, in detail, how to add new features to your project, especially ones you were not able to complete by the deadline
To add a new brick type to the project, the programmer would need to add a line of code to import/assign the .gif
 file to a variable to be passed to the CreateBrick() function, there would also need to be an expansion of the "if
 " loop that determined specific properties of each added brick type. The same would need to be done with power-ups
 . It would be decently difficult to add an additional ball or paddle to the project as most of the code relies on
  the fact that there is only a singular one of each of these objects. These are things that I've since learned how
   to fix and would/will know to design around in future projects

