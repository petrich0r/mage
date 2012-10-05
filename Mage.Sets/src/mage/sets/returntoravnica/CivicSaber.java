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
package mage.sets.returntoravnica;
 
import java.util.UUID;
import mage.Constants;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.common.continious.BoostEquippedEffect;
import mage.abilities.keyword.EquipAbility;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
 
/**
 *
 * @author LevelX2
 */
public class CivicSaber extends CardImpl<CivicSaber> {
 
    public CivicSaber(UUID ownerId) {
        super(ownerId, 227, "Civic Saber", Constants.Rarity.UNCOMMON, new Constants.CardType[]{Constants.CardType.ARTIFACT}, "{1}");
        this.expansionSetCode = "RTR";
        this.subtype.add("Equipment");
 
        // Equipped creature gets +1/+0 for each of its colors.
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, new BoostEquippedEffect(new CivicSaberColorCount(), new StaticValue(0), Constants.Duration.WhileOnBattlefield)));
        // Equip {1}
        this.addAbility(new EquipAbility(Constants.Outcome.AddAbility, new GenericManaCost(1)));
    }
 
    public CivicSaber(final CivicSaber card) {
        super(card);
    }
 
    @Override
    public CivicSaber copy() {
        return new CivicSaber(this);
    }
}
 
class CivicSaberColorCount implements DynamicValue {
 
    public CivicSaberColorCount() {
    }
 
    public CivicSaberColorCount(final CivicSaberColorCount dynamicValue) {
    }
 
    @Override
    public int calculate(Game game, Ability source) {
        int count = 0;
        Permanent equipment = game.getPermanent(source.getSourceId());
        if (equipment != null) {
            Permanent permanent = game.getPermanent(equipment.getAttachedTo());
            if (permanent != null) {
                count = permanent.getColor().getColorCount();
            }
        }
        return count;
    }
 
    @Override
    public DynamicValue clone() {
        return new CivicSaberColorCount(this);
    }
 
    @Override
    public String toString() {
        return "1";
    }
 
    @Override
    public String getMessage() {
        return "of its colors";
    }
}
 