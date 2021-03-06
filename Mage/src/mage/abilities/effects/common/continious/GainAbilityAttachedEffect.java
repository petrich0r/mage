/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
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

package mage.abilities.effects.common.continious;

import mage.constants.AttachmentType;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SubLayer;
import mage.abilities.Ability;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.targetpointer.FixedTarget;

/**
 * @author BetaSteward_at_googlemail.com
 */
public class GainAbilityAttachedEffect extends ContinuousEffectImpl<GainAbilityAttachedEffect> {

    protected Ability ability;
    protected AttachmentType attachmentType;
    protected boolean fixedTarget = false;

    public GainAbilityAttachedEffect(Ability ability, AttachmentType attachmentType, Duration duration, String rule) {
        super(duration, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.AddAbility);
        this.ability = ability;
        this.attachmentType = attachmentType;
        this.duration = duration;
        if (duration == Duration.EndOfTurn) {
            fixedTarget = true;
        }
        this.staticText = rule;
    }

    public GainAbilityAttachedEffect(Ability ability, AttachmentType attachmentType, Duration duration) {
        super(duration, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.AddAbility);
        this.ability = ability;
        this.attachmentType = attachmentType;
        this.duration = duration;
        if (duration == Duration.EndOfTurn) {
            fixedTarget = true;
        }
        setText();
    }

    public GainAbilityAttachedEffect(Ability ability, AttachmentType attachmentType) {
        super(Duration.WhileOnBattlefield, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.AddAbility);
        this.ability = ability;
        this.attachmentType = attachmentType;
        setText();
    }

    public GainAbilityAttachedEffect(final GainAbilityAttachedEffect effect) {
        super(effect);
        this.ability = effect.ability.copy();
        this.attachmentType = effect.attachmentType;
        this.fixedTarget = effect.fixedTarget;
    }

    @Override
    public GainAbilityAttachedEffect copy() {
        return new GainAbilityAttachedEffect(this);
    }

    @Override
    public void init(Ability source, Game game) {
        super.init(source, game);
        if (fixedTarget) {
            Permanent equipment = game.getPermanent(source.getSourceId());
            if (equipment != null && equipment.getAttachedTo() != null) {
                this.setTargetPointer(new FixedTarget(equipment.getAttachedTo()));
            }
        }
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent creature = null;
        if (fixedTarget) {
            creature = game.getPermanent(targetPointer.getFirst(game, source));
        } else {
            Permanent equipment = game.getPermanent(source.getSourceId());
            if (equipment != null && equipment.getAttachedTo() != null) {
                creature = game.getPermanent(equipment.getAttachedTo());
            }
        }
        if (creature != null) {
            creature.addAbility(ability, source.getSourceId(), game);
        }
        return true;
    }

    private void setText() {
        StringBuilder sb = new StringBuilder();
        if (attachmentType == AttachmentType.AURA) {
            sb.append("Enchanted");
        } else if (attachmentType == AttachmentType.EQUIPMENT) {
            sb.append("Equipped");
        }
        sb.append(" creature ");
        if (duration == Duration.WhileOnBattlefield) {
            sb.append("has ");
        } else {
            sb.append("gains ");
        }
        sb.append(ability.getRule());
        if (!duration.toString().isEmpty()) {
            sb.append(" ").append(duration.toString());
        }
        staticText = sb.toString();
    }

}
