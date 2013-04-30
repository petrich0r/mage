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

package mage.sets.dragonsmaze;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Duration;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.delayed.AtEndOfTurnDelayedTriggeredAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ReturnFromExileEffect;
import mage.abilities.effects.common.UnblockableSourceEffect;
import mage.abilities.effects.common.continious.BoostSourceEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;


/**
 *
 * @author LevelX2
 */


public class AEtherling extends CardImpl<AEtherling> {

    public AEtherling (UUID ownerId) {
        super(ownerId, 11, "AEtherling", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{4}{U}{U}");
        this.expansionSetCode = "DGM";
        this.subtype.add("Shapeshifter");
        this.color.setBlue(true);
        this.power = new MageInt(4);
        this.toughness = new MageInt(5);

        // {U}: Exile AEtherling. Return it to the battlefield under its owner's control at the beginning of the next end step.
        this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new AEherlingRemovingEffect(), new ManaCostsImpl("{U}")));
        // {U}: AEtherling is unblockable this turn
        this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new UnblockableSourceEffect(), new ManaCostsImpl("{U}")));
        // {1}: AEtherling gets +1/-1 until end of turn.
        this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new BoostSourceEffect(1, -1, Duration.EndOfTurn), new ManaCostsImpl("{1}")));
        // {1}: AEtherling gets -1/+1 until end of turn
        this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new BoostSourceEffect(-1, 1, Duration.EndOfTurn), new ManaCostsImpl("{1}")));
    }

    public AEtherling (final AEtherling card) {
        super(card);
    }

    @Override
    public AEtherling copy() {
        return new AEtherling(this);
    }

}
class AEherlingRemovingEffect extends OneShotEffect<AEherlingRemovingEffect> {

    private static final String effectText = "Exile {this}. Return it to the battlefield under its owner's control at the beginning of the next end step";

    AEherlingRemovingEffect () {
        super(Outcome.Benefit);
        staticText = effectText;
    }

    AEherlingRemovingEffect(AEherlingRemovingEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(source.getSourceId());
        if (permanent != null) {
            if (permanent.moveToExile(source.getSourceId(), "AEherling Exile", source.getId(), game)) {
                //create delayed triggered ability
                AtEndOfTurnDelayedTriggeredAbility delayedAbility = new AtEndOfTurnDelayedTriggeredAbility(
                        new ReturnFromExileEffect(source.getSourceId(), Zone.BATTLEFIELD));
                delayedAbility.setSourceId(source.getSourceId());
                delayedAbility.setControllerId(source.getControllerId());
                game.addDelayedTriggeredAbility(delayedAbility);
                return true;
            }
        }
        return false;
    }

    @Override
    public AEherlingRemovingEffect copy() {
        return new AEherlingRemovingEffect(this);
    }

}