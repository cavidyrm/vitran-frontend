package com.vitran.shop.ui.sections.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.vitran.shop.ui.components.admin.AdminTaxonomyNode
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_taxonomy_animals
import vitranshop.shared.generated.resources.admin_taxonomy_animals_food
import vitranshop.shared.generated.resources.admin_taxonomy_animals_supplies
import vitranshop.shared.generated.resources.admin_taxonomy_apparel
import vitranshop.shared.generated.resources.admin_taxonomy_audio
import vitranshop.shared.generated.resources.admin_taxonomy_baby
import vitranshop.shared.generated.resources.admin_taxonomy_baby_clothing
import vitranshop.shared.generated.resources.admin_taxonomy_beauty
import vitranshop.shared.generated.resources.admin_taxonomy_beverages
import vitranshop.shared.generated.resources.admin_taxonomy_clothing
import vitranshop.shared.generated.resources.admin_taxonomy_cosmetics
import vitranshop.shared.generated.resources.admin_taxonomy_electronics
import vitranshop.shared.generated.resources.admin_taxonomy_fitness
import vitranshop.shared.generated.resources.admin_taxonomy_food
import vitranshop.shared.generated.resources.admin_taxonomy_furniture
import vitranshop.shared.generated.resources.admin_taxonomy_grocery
import vitranshop.shared.generated.resources.admin_taxonomy_home
import vitranshop.shared.generated.resources.admin_taxonomy_kitchen
import vitranshop.shared.generated.resources.admin_taxonomy_pants
import vitranshop.shared.generated.resources.admin_taxonomy_phones
import vitranshop.shared.generated.resources.admin_taxonomy_shoes
import vitranshop.shared.generated.resources.admin_taxonomy_skincare
import vitranshop.shared.generated.resources.admin_taxonomy_sporting
import vitranshop.shared.generated.resources.admin_taxonomy_sports
import vitranshop.shared.generated.resources.admin_taxonomy_toys
import vitranshop.shared.generated.resources.admin_taxonomy_tshirts

/** Shopify-shaped Standard Product Taxonomy tree. Mock labels only. */
@Composable
fun rememberProductTaxonomy(): List<AdminTaxonomyNode> {
    val animals = stringResource(Res.string.admin_taxonomy_animals)
    val animalsFood = stringResource(Res.string.admin_taxonomy_animals_food)
    val animalsSupplies = stringResource(Res.string.admin_taxonomy_animals_supplies)
    val apparel = stringResource(Res.string.admin_taxonomy_apparel)
    val clothing = stringResource(Res.string.admin_taxonomy_clothing)
    val tshirts = stringResource(Res.string.admin_taxonomy_tshirts)
    val pants = stringResource(Res.string.admin_taxonomy_pants)
    val shoes = stringResource(Res.string.admin_taxonomy_shoes)
    val beauty = stringResource(Res.string.admin_taxonomy_beauty)
    val cosmetics = stringResource(Res.string.admin_taxonomy_cosmetics)
    val skincare = stringResource(Res.string.admin_taxonomy_skincare)
    val home = stringResource(Res.string.admin_taxonomy_home)
    val furniture = stringResource(Res.string.admin_taxonomy_furniture)
    val kitchen = stringResource(Res.string.admin_taxonomy_kitchen)
    val electronics = stringResource(Res.string.admin_taxonomy_electronics)
    val phones = stringResource(Res.string.admin_taxonomy_phones)
    val audio = stringResource(Res.string.admin_taxonomy_audio)
    val food = stringResource(Res.string.admin_taxonomy_food)
    val grocery = stringResource(Res.string.admin_taxonomy_grocery)
    val beverages = stringResource(Res.string.admin_taxonomy_beverages)
    val baby = stringResource(Res.string.admin_taxonomy_baby)
    val babyClothing = stringResource(Res.string.admin_taxonomy_baby_clothing)
    val toys = stringResource(Res.string.admin_taxonomy_toys)
    val sports = stringResource(Res.string.admin_taxonomy_sports)
    val fitness = stringResource(Res.string.admin_taxonomy_fitness)
    val sporting = stringResource(Res.string.admin_taxonomy_sporting)
    return remember(
        animals, animalsFood, animalsSupplies, apparel, clothing, tshirts, pants, shoes,
        beauty, cosmetics, skincare, home, furniture, kitchen, electronics, phones, audio,
        food, grocery, beverages, baby, babyClothing, toys, sports, fitness, sporting,
    ) {
        listOf(
            AdminTaxonomyNode(
                id = "animals-pet",
                label = animals,
                children = listOf(
                    AdminTaxonomyNode("animals-pet/food", animalsFood),
                    AdminTaxonomyNode("animals-pet/supplies", animalsSupplies),
                ),
            ),
            AdminTaxonomyNode(
                id = "apparel-accessories",
                label = apparel,
                children = listOf(
                    AdminTaxonomyNode(
                        id = "apparel-accessories/clothing",
                        label = clothing,
                        children = listOf(
                            AdminTaxonomyNode("apparel-accessories/clothing/t-shirts", tshirts),
                            AdminTaxonomyNode("apparel-accessories/clothing/pants", pants),
                        ),
                    ),
                    AdminTaxonomyNode("apparel-accessories/shoes", shoes),
                ),
            ),
            AdminTaxonomyNode(
                id = "beauty-health",
                label = beauty,
                children = listOf(
                    AdminTaxonomyNode("beauty-health/cosmetics", cosmetics),
                    AdminTaxonomyNode("beauty-health/skincare", skincare),
                ),
            ),
            AdminTaxonomyNode(
                id = "home-garden",
                label = home,
                children = listOf(
                    AdminTaxonomyNode("home-garden/furniture", furniture),
                    AdminTaxonomyNode("home-garden/kitchen", kitchen),
                ),
            ),
            AdminTaxonomyNode(
                id = "electronics",
                label = electronics,
                children = listOf(
                    AdminTaxonomyNode("electronics/phones", phones),
                    AdminTaxonomyNode("electronics/audio", audio),
                ),
            ),
            AdminTaxonomyNode(
                id = "food-beverage",
                label = food,
                children = listOf(
                    AdminTaxonomyNode("food-beverage/grocery", grocery),
                    AdminTaxonomyNode("food-beverage/beverages", beverages),
                ),
            ),
            AdminTaxonomyNode(
                id = "baby-toddler",
                label = baby,
                children = listOf(
                    AdminTaxonomyNode("baby-toddler/clothing", babyClothing),
                    AdminTaxonomyNode("baby-toddler/toys", toys),
                ),
            ),
            AdminTaxonomyNode(
                id = "sports-fitness",
                label = sports,
                children = listOf(
                    AdminTaxonomyNode("sports-fitness/equipment", fitness),
                    AdminTaxonomyNode("sports-fitness/sporting", sporting),
                ),
            ),
        )
    }
}
