/*
* Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are
* permitted provided that the following conditions are met:
*
*    1. Redistributions of source code must retain the above copyright notice, this list of
*       conditions and the following disclaimer.
*
*    2. Redistributions in binary form must reproduce the above copyright notice, this list
*       of conditions and the following disclaimer in the documentation and/or other materials
*       provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
* FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation are those of the
* authors and should not be interpreted as representing official policies, either expressed
* or implied, of BetaSteward_at_googlemail.com.
*/

package mage.abilities.common;

import java.util.UUID;
import mage.abilities.ActivatedAbilityImpl;
import mage.abilities.costs.Cost;
import mage.abilities.effects.Effect;
import mage.constants.Zone;
import mage.game.Game;
import mage.util.CardUtil;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ActivateOncePerTurnActivatedAbility extends ActivatedAbilityImpl<ActivateOncePerTurnActivatedAbility> {

    public ActivateOncePerTurnActivatedAbility(Zone zone, Effect effect, Cost cost) {
        super(zone, effect, cost);
    }

    public ActivateOncePerTurnActivatedAbility(ActivateOncePerTurnActivatedAbility ability) {
        super(ability);
    }

    @Override
    public boolean canActivate(UUID playerId, Game game) {
        if (super.canActivate(playerId, game)) {
            Boolean activated = (Boolean)game.getState().getValue(CardUtil.getCardZoneString("activated", sourceId, game));
            if (activated == null) {
                return true;
            }
            else {
                return !activated;
            }
        }
        return false;
    }

    @Override
    public boolean activate(Game game, boolean noMana) {
        if (canActivate(this.controllerId, game)) {
            if (super.activate(game, noMana)) {
                game.getState().setValue(CardUtil.getCardZoneString("activated", sourceId, game), Boolean.TRUE);
                return true;
            }
        }
        return false;
    }

    @Override
    public void reset(Game game) {
        game.getState().setValue(CardUtil.getCardZoneString("activated", sourceId, game), Boolean.FALSE);
    }

    @Override
    public boolean resolve(Game game) {
        if (super.resolve(game)) {
            return true;
        }
        return false;
    }

    @Override
    public String getRule() {
        return super.getRule() + " Activate this ability only once each turn.";
    }

    @Override
    public ActivateOncePerTurnActivatedAbility copy() {
        return new ActivateOncePerTurnActivatedAbility(this);
    }

}
