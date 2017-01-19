package me.quazi.bc2017.garden;

import battlecode.common.*;

public strictfp class RobotPlayer {

    static RobotController rc;

    //core code
    static int motionAvoid() {
        // determine whether bullet any bullet is heading towards bot

        try {
            BulletInfo[] x = rc.senseNearbyBullets();
            boolean perpendicular = false;
            Direction targetDir = null;
            if (x.length > 0) {

                for (int i = 0; i < x.length; i++) {
                    BulletInfo nearest = x[i];
                    targetDir = nearest.getLocation().directionTo(rc.getLocation());
                    if (nearest.getDir() == targetDir) {
                        // move perpendicular to the nearest bullet heading in this direction
                        perpendicular = true;
                        break;
                    }
                }
            }

            if (perpendicular) {
                targetDir.rotateLeftRads((float) (Math.PI / 2));
                if (rc.canMove(targetDir) && !(rc.hasMoved())) {
                    rc.move(targetDir, rc.getType().strideRadius);

                } else if (rc.canMove(targetDir.rotateRightRads((float) (Math.PI))) && !(rc.hasMoved())) {
                    rc.move(targetDir.rotateRightRads((float) (Math.PI)), rc.getType().strideRadius);
                }

            } else {
                Direction motions;
                do {
                    motions = new Direction((float) (2 * Math.PI * Math.random()));
                    if (rc.canMove(motions)) {
                        rc.move(motions, rc.getType().strideRadius);
                    }

                } while (!(rc.hasMoved()));
            }
        } catch (Exception ex) {
            System.out.println("motion avoid error:");
            ex.printStackTrace();
        }

        return Clock.getBytecodesLeft();
    }

    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;


        switch (rc.getType()) {
            case ARCHON:
                archon();
                break;
            case GARDENER:
                gardener();
                break;
            case LUMBERJACK:
            case SOLDIER:
        }
    }

    static void archon() throws GameActionException {
        while (true) {
            // catch exceptions to prevent exploding bots
            try {
                motionAvoid();

                if (rc.getTeamBullets() > 150) {
                    rc.donate(GameConstants.BULLET_EXCHANGE_RATE);
                    Direction x = new Direction((float) (Math.PI * Math.random()));
                    if (rc.canHireGardener(x)) {
                        rc.hireGardener(x);
                    }
                }
                Clock.yield();
            } catch (Exception ex) {
                System.out.println("archon error:");
                ex.printStackTrace();
            }
        }
    }

    static void gardener() throws GameActionException {
        while (true) {
            // catch exceptions to prevent exploding bots
            try {
                for (int i = 0; i < 5; i++) {
                    motionAvoid();
                    Clock.yield();
                }
                Direction tree = new Direction(0);
                int c = 0;
                do {
                    tree.rotateLeftRads((float) (Math.PI / 32));
                    if (rc.canPlantTree(tree)) {
                        rc.plantTree(tree);
                        System.out.println("planted tree!");
                        Clock.yield();
                    }
                    c++;
                } while (c < 64);

                TreeInfo[] z = rc.senseNearbyTrees();
                for (int i = 0; i < z.length; i++) {
                    MapLocation t = z[i].getLocation();
                    if (z[i].getTeam() == rc.getTeam().opponent()) {
                        if (rc.canChop(t)) {
                            rc.chop(t);
                        }
                    } else {
                        if (rc.canShake(t)) {
                            rc.shake(t);
                        }
                        if (rc.canWater(t)) {
                            rc.water(t);
                        }
                    }
                    Clock.yield();
                }
                Clock.yield();
            } catch (Exception ex) {
                System.out.println("archon error:");
                ex.printStackTrace();
            }
        }
    }


}

