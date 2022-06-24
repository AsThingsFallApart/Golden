# project2rootuserscript.sql
# The root user execution script for Project Two - CNT 4714 - Summer 2022
# all commands assumed to be executed by the root user

# Command 1:
#   Query: Which rider won Paris Roubaix Femmes in 2021?
SELECT ridername
FROM racewinners
WHERE racename = 'Paris Roubaix Femmes' AND raceyear = 2021;

# Command 2:
#   Query: List the teams that ride Colnago bikes.
SELECT teamname
FROM teams
WHERE bikename = "Colnago";
                   
# Command 3:
#   Query: List the name of every race won by a rider who has more than
#          50 professional wins.
SELECT DISTINCT racename
FROM racewinners
WHERE ridername IN (SELECT ridername 
                    FROM riders
                    WHERE num_pro_wins > 50);

# Command 4:
#   Query: List the names of all the riders on the same team as the winner of the 
#          2010 Paris-Roubaix race.
SELECT ridername 
FROM riders 
WHERE teamname = (SELECT teamname
                  FROM riders
                  WHERE ridername = (SELECT ridername
                                     FROM racewinners
                                     WHERE racename = 'Paris-Roubaix' AND raceyear = 2010
                                    )
                );
                
# Commands 5A, 5B, and 5C:
#    Insert the rider Mark Renshaw into the riders table.
# * * Do a "before" and "after" selection on the riders table
SELECT * FROM riders;

INSERT INTO riders VALUES('Mark Renshaw','HTC-Columbia','Australia',26, 'M');

SELECT * FROM riders;


# Command 6:
#   List the names of those riders who have won Paris-Roubaix at least two times.
SELECT ridername 
FROM racewinners
WHERE racename = 'Paris-Roubaix'
GROUP BY ridername
HAVING count(ridername) >= 2;

# Commands 7A, 7B, and 7C:
#   Delete all the riders from Belgium from the riders table.
#   * * * Do a "before" and "after" select * from riders for this command.
SELECT * FROM riders;

DELETE FROM riders WHERE nationality = 'Belgium';

SELECT * FROM riders;

# Commands 8A, 8B, and 8C:
#    Update rider Mark Renshaw to show number of wins = 30 in the riders table.
# * * Do a "before" and "after" selection on the riders table
SELECT * FROM riders;

UPDATE riders SET num_pro_wins = 30 WHERE ridername = "Mark Renshaw";

SELECT * FROM riders;

# Command 9:
#   This command is malformed and will not execute
SELECT * 
FROM racewinners
WHERE length >= 200;
