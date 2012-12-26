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
package mage.sets.timespiral;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.abilities.Ability;
import mage.abilities.costs.common.SacrificeTargetCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.BuybackAbility;
import mage.cards.CardImpl;
import mage.filter.common.FilterLandPermanent;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.game.Game;
import mage.game.turn.TurnMod;
import mage.target.TargetPlayer;
import mage.target.common.TargetControlledPermanent;

/**
 *
 * @author LevelX2
 */
public class WalkTheAeons extends CardImpl<WalkTheAeons> {

    private static final FilterLandPermanent filter = new FilterLandPermanent("three Islands");

    static {
        filter.add(new SubtypePredicate("Island"));
    }

    public WalkTheAeons(UUID ownerId) {
        super(ownerId, 93, "Walk the Aeons", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{4}{U}{U}");
        this.expansionSetCode = "TSP";
        this.color.setBlue(true);

        // Buyback—Sacrifice three Islands. (You may sacrifice three Islands in addition to any other costs as you cast this spell. If you do, put this card into your hand as it resolves.)
        this.addAbility(new BuybackAbility(new SacrificeTargetCost(new TargetControlledPermanent(3,3, filter, true))));

        // Target player takes an extra turn after this one.
        this.getSpellAbility().addEffect(new ExtraTurnEffect());
        this.getSpellAbility().addTarget(new TargetPlayer());
    }

    public WalkTheAeons(final WalkTheAeons card) {
        super(card);
    }

    @Override
    public WalkTheAeons copy() {
        return new WalkTheAeons(this);
    }
}

class ExtraTurnEffect extends OneShotEffect<ExtraTurnEffect> {

    public ExtraTurnEffect() {
        super(Outcome.ExtraTurn);
        staticText = "Target player takes an extra turn after this one";
    }

    public ExtraTurnEffect(final ExtraTurnEffect effect) {
        super(effect);
    }

    @Override
    public ExtraTurnEffect copy() {
        return new ExtraTurnEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        game.getState().getTurnMods().add(new TurnMod(getTargetPointer().getFirst(game, source), false));
        return true;
    }

}