/*
 *  Copyright 2011 BetaSteward_at_googlemail.com. All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 * 
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 * 
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 * 
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.player.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.ActivatedAbility;
import mage.abilities.PlayLandAbility;
import mage.abilities.common.PassAbility;
import mage.cards.Card;
import mage.game.Game;
import mage.game.combat.Combat;
import mage.game.combat.CombatGroup;
import mage.game.turn.Step.StepPart;
import mage.players.Player;
import org.apache.log4j.Logger;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class MCTSNode {

    private static final double selectionCoefficient = 1.0;
    private static final double passRatioTolerance = 0.0;
     private static final transient Logger logger = Logger.getLogger(MCTSNode.class);

    private int visits = 0;
    private int wins = 0;
    private MCTSNode parent;
    private List<MCTSNode> children = new ArrayList<MCTSNode>();
    private Ability action;
    private Game game;
    private Combat combat;
    private String stateValue;
    private UUID playerId;
    private boolean terminal = false;

    private static int nodeCount;

    public MCTSNode(Game game) {
        this.game = game;
        this.stateValue = game.getState().getValue(false, game);
        this.terminal = game.isGameOver();
        setPlayer();
        nodeCount = 1;
    }    

    protected MCTSNode(MCTSNode parent, Game game, Ability action) {
        this.game = game;
        this.stateValue = game.getState().getValue(false, game);
        this.terminal = game.isGameOver();
        this.parent = parent;
        this.action = action;
        setPlayer();
        nodeCount++;
    }

    protected MCTSNode(MCTSNode parent, Game game, Combat combat) {
        this.game = game;
        this.combat = combat;
        this.stateValue = game.getState().getValue(false, game);
        this.terminal = game.isGameOver();
        this.parent = parent;
        setPlayer();
        nodeCount++;
    }

    private void setPlayer() {
        if (game.getStep().getStepPart() == StepPart.PRIORITY)
            playerId = game.getPriorityPlayerId();
        else {
            if (game.getStep().getType() == PhaseStep.DECLARE_BLOCKERS)
                playerId = game.getCombat().getDefenders().iterator().next();
            else
                playerId = game.getActivePlayerId();
        }
    }

    public MCTSNode select(UUID targetPlayerId) {
        double bestValue = Double.NEGATIVE_INFINITY;
        boolean isTarget = playerId.equals(targetPlayerId);
        MCTSNode bestChild = null;
        if (children.size() == 1) {
            return children.get(0);
        }
        for (MCTSNode node: children) {
            double uct;
            if (node.visits > 0)
                if (isTarget)
                    uct = (node.wins / (node.visits)) + (selectionCoefficient * Math.sqrt(Math.log(visits) / (node.visits)));
                else
                    uct = ((node.visits - node.wins) / (node.visits)) + (selectionCoefficient * Math.sqrt(Math.log(visits) / (node.visits)));
            else
                // ensure that a random unvisited node is played first
                uct = 10000 + 1000 * Math.random();
            if (uct > bestValue) {
                bestChild = node;
                bestValue = uct;
            }
        }
        return bestChild;
    }

    public void expand() {
        MCTSPlayer player = (MCTSPlayer) game.getPlayer(playerId);
        if (player.getNextAction() == null) {
            logger.fatal("next action is null");
        }
        switch (player.getNextAction()) {
            case PRIORITY:
//                logger.info("Priority for player:" + player.getName() + " turn: " + game.getTurnNum() + " phase: " + game.getPhase().getType() + " step: " + game.getStep().getType());
                List<Ability> abilities = player.getPlayableOptions(game);
                for (Ability ability: abilities) {
                    Game sim = game.copy();
//                    logger.info("expand " + ability.toString());
                    MCTSPlayer simPlayer = (MCTSPlayer) sim.getPlayer(player.getId());
                    simPlayer.activateAbility((ActivatedAbility)ability, sim);
                    sim.resume();
                    children.add(new MCTSNode(this, sim, ability));
                }
                break;
            case SELECT_ATTACKERS:
//                logger.info("Select attackers:" + player.getName());
                List<List<UUID>> attacks = player.getAttacks(game);
                UUID defenderId = game.getOpponents(player.getId()).iterator().next();
                for (List<UUID> attack: attacks) {
                    Game sim = game.copy();
                    MCTSPlayer simPlayer = (MCTSPlayer) sim.getPlayer(player.getId());
                    for (UUID attackerId: attack) {
                        simPlayer.declareAttacker(attackerId, defenderId, sim);
                    }
                    sim.resume();
                    children.add(new MCTSNode(this, sim, sim.getCombat()));
                }
                break;
            case SELECT_BLOCKERS:
//                logger.info("Select blockers:" + player.getName());
                List<List<List<UUID>>> blocks = player.getBlocks(game);
                for (List<List<UUID>> block: blocks) {
                    Game sim = game.copy();
                    MCTSPlayer simPlayer = (MCTSPlayer) sim.getPlayer(player.getId());
                    List<CombatGroup> groups = sim.getCombat().getGroups();
                    for (int i = 0; i < groups.size(); i++) {
                        if (i < block.size()) {
                            for (UUID blockerId: block.get(i)) {
                                simPlayer.declareBlocker(simPlayer.getId(), blockerId, groups.get(i).getAttackers().get(0), sim);
                            }
                        }
                    }
                    sim.resume();
                    children.add(new MCTSNode(this, sim, sim.getCombat()));
                }
                break;
        }
        game = null;
    }

    public int simulate(UUID playerId) {
//        long startTime = System.nanoTime();
        Game sim = createSimulation(game, playerId);
        sim.resume();
//        long duration = System.nanoTime() - startTime;
        int retVal = -1;  //anything other than a win is a loss
        for (Player simPlayer: sim.getPlayers().values()) {
//            logger.info(simPlayer.getName() + " calculated " + ((SimulatedPlayerMCTS)simPlayer).getActionCount() + " actions in " + duration/1000000000.0 + "s");
            if (simPlayer.getId().equals(playerId) && simPlayer.hasWon()) {
//                logger.info("AI won the simulation");
                retVal = 1;
            }
        }
        return retVal;
    }

    public void backpropagate(int result) {
        if (result == 0)
            return;
        if (result == 1)
            wins++;
        visits++;
        if (parent != null)
            parent.backpropagate(result);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public MCTSNode bestChild() {
        if (children.size() == 1)
            return children.get(0);
        double bestCount = -1;
        double bestRatio = 0;
        boolean bestIsPass = false;
        MCTSNode bestChild = null;
        for (MCTSNode node: children) {
            //favour passing vs any other action except for playing land if ratio is close
            if (node.visits > bestCount) {
                if (bestIsPass) {
                    double ratio = node.wins/(node.visits * 1.0);
                    if (ratio < bestRatio + passRatioTolerance)
                        continue;
                }
                bestChild = node;
                bestCount = node.visits;
                bestRatio = node.wins/(node.visits * 1.0);
                bestIsPass = false;
            }
            else if (node.action instanceof PassAbility && node.visits > 10 && !(bestChild.action instanceof PlayLandAbility)) {
                //favour passing vs any other action if ratio is close
                double ratio = node.wins/(node.visits * 1.0);
                if (ratio > bestRatio - passRatioTolerance) {
                    logger.info("choosing pass over " + bestChild.getAction());
                    bestChild = node;
                    bestCount = node.visits;
                    bestRatio = ratio;
                    bestIsPass = true;
                }
            }
        }
        return bestChild;
    }

    public void emancipate() {
        if (parent != null) {
            this.parent.children.remove(this);
            this.parent = null;
        }
    }

    public Ability getAction() {
        return action;
    }

    public int getNumChildren() {
        return children.size();
    }

    public MCTSNode getParent() {
        return parent;
    }

    public Combat getCombat() {
        return combat;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public String getStateValue() {
        return stateValue;
    }

    public double getWinRatio() {
        if (visits > 0)
            return wins/(visits * 1.0);
        return -1.0;
    }

    public int getVisits() {
        return visits;
    }

    /**
     * Copies game and replaces all players in copy with simulated players
     * Shuffles each players library so that there is no knowledge of its order
     *
     * @param game
     * @return a new game object with simulated players
     */
    protected Game createSimulation(Game game, UUID playerId) {
        Game sim = game.copy();

        for (Player copyPlayer: sim.getState().getPlayers().values()) {
            Player origPlayer = game.getState().getPlayers().get(copyPlayer.getId()).copy();
            SimulatedPlayerMCTS newPlayer = new SimulatedPlayerMCTS(copyPlayer.getId(), true);
            newPlayer.restore(origPlayer);
            sim.getState().getPlayers().put(copyPlayer.getId(), newPlayer);
        }
        randomizePlayers(sim, playerId);
        sim.setSimulation(true);
        return sim;
    }

    /*
     * Shuffles each players library so that there is no knowledge of its order
     * Swaps all other players hands with random cards from the library so that
     * there is no knowledge of what cards are in opponents hands
     */
    protected void randomizePlayers(Game game, UUID playerId) {
        for (Player player: game.getState().getPlayers().values()) {
            if (!player.getId().equals(playerId)) {
                int handSize = player.getHand().size();
                player.getLibrary().addAll(player.getHand().getCards(game), game);
                player.getHand().clear();
                player.getLibrary().shuffle();
                for (int i = 0; i < handSize; i++) {
                    Card card = player.getLibrary().removeFromTop(game);
                    game.setZone(card.getId(), Zone.HAND);
                    player.getHand().add(card);
                }
            }
            else {
                player.getLibrary().shuffle();                
            }
        }
    }

    public boolean isTerminal() {
        return terminal;
    }

    public boolean isWinner(UUID playerId) {
        if (game != null) {
            Player player = game.getPlayer(playerId);
            if (player != null && player.hasWon())
                return true;
        }
        return false;
    }

    /**
     * 
     * performs a breadth first search for a matching game state
     * 
     * @param state - the game state that we are looking for
     * @param nextAction - the next action that will be performed
     * @return the matching state or null if no match is found
     */
    public MCTSNode getMatchingState(String state) {
        ArrayDeque<MCTSNode> queue = new ArrayDeque<MCTSNode>();
        queue.add(this);

        while (!queue.isEmpty()) {
            MCTSNode current = queue.remove();
            if (current.stateValue.equals(state))
                return current;
            for (MCTSNode child: current.children) {
                queue.add(child);
            }
        }
        return null;
    }

    public void merge(MCTSNode merge) {
        if (!stateValue.equals(merge.stateValue)) {
            logger.info("mismatched merge states");
            return;
        }

        this.visits += merge.visits;
        this.wins += merge.wins;

        List<MCTSNode> mergeChildren = new ArrayList<MCTSNode>();
        for (MCTSNode child: merge.children) {
            mergeChildren.add(child);
        }

        for (MCTSNode child: children) {
            for (MCTSNode mergeChild: mergeChildren) {
                if (mergeChild.action != null && child.action != null) {
                    if (mergeChild.action.toString().equals(child.action.toString())) {
                        if (!mergeChild.stateValue.equals(child.stateValue)) {
                            logger.info("mismatched merge states");
                            mergeChildren.remove(mergeChild);
                        }
                        else {
                            child.merge(mergeChild);
                            mergeChildren.remove(mergeChild);
                        }
                        break;
                    }
                }
                else {
                    if (mergeChild.combat.getValue().equals(child.combat.getValue())) {
                        if (!mergeChild.stateValue.equals(child.stateValue)) {
                            logger.info("mismatched merge states");
                            mergeChildren.remove(mergeChild);
                        }
                        else {
                            child.merge(mergeChild);
                            mergeChildren.remove(mergeChild);
                        }
                        break;
                    }
                }
            }
        }
        if (!mergeChildren.isEmpty()) {
            for (MCTSNode child: mergeChildren) {
                child.parent = this;
                children.add(child);
            }
        }
    }

//    public void print(int depth) {
//        String indent = String.format("%1$-" + depth + "s", "");
//        StringBuilder sb = new StringBuilder();
//        MCTSPlayer player = (MCTSPlayer) game.getPlayer(playerId);
//        sb.append(indent).append(player.getName()).append(" ").append(visits).append(":").append(wins).append(" - ");
//        if (action != null)
//            sb.append(action.toString());
//        System.out.println(sb.toString());
//        for (MCTSNode child: children) {
//            child.print(depth + 1);
//        }
//    }

    public int size() {
        int num = 1;
        for (MCTSNode child: children) {
            num += child.size();
        }
        return num;
    }

}
