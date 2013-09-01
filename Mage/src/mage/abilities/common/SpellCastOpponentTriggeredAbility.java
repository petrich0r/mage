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
package mage.abilities.common;

import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.Effect;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.stack.Spell;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class SpellCastOpponentTriggeredAbility extends TriggeredAbilityImpl<SpellCastOpponentTriggeredAbility> {

    private static final FilterCard spellCard = new FilterCard("a spell");
    protected FilterCard filter;
    protected boolean setTargetPointerPlayer;

    public SpellCastOpponentTriggeredAbility(Effect effect, boolean optional) {
        this(effect, spellCard, optional);
    }

    public SpellCastOpponentTriggeredAbility(Effect effect, FilterCard filter, boolean optional) {
        this(Zone.BATTLEFIELD, effect, filter, optional);
    }

    public SpellCastOpponentTriggeredAbility(Zone zone, Effect effect, FilterCard filter, boolean optional) {
        this(zone, effect, filter, optional, false);
    }

    public SpellCastOpponentTriggeredAbility(Zone zone, Effect effect, FilterCard filter, boolean optional, boolean setTargetPointerPlayer) {
        super(zone, effect, optional);
        this.filter = filter;
        this.setTargetPointerPlayer = setTargetPointerPlayer;
    }

    public SpellCastOpponentTriggeredAbility(final SpellCastOpponentTriggeredAbility ability) {
        super(ability);
        this.filter = ability.filter;
        this.setTargetPointerPlayer = ability.setTargetPointerPlayer;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.SPELL_CAST && game.getOpponents(controllerId).contains(event.getPlayerId())) {
            Spell spell = game.getStack().getSpell(event.getTargetId());
            if (spell != null && filter.match(spell, game)) {
                if (setTargetPointerPlayer) {
                    for (Effect effect: this.getEffects()) {
                        effect.setTargetPointer(new FixedTarget(event.getPlayerId()));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever an opponent casts " + filter.getMessage() + ", " + super.getRule();
    }

    @Override
    public SpellCastOpponentTriggeredAbility copy() {
        return new SpellCastOpponentTriggeredAbility(this);
    }
}