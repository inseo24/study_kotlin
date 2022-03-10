package screen

import LINE_DIVIDER
import data.CartItems
import data.Product
import extensions.getNotEmptyInt
import extensions.getNotEmptyString

class ShoppingProductList : Screen() {
    private val products = arrayOf(
        Product("패션", "패딩"),
        Product("패션", "바지"),
        Product("전자기기", "핸드폰"),
        Product("전자기기", "아이패드"),
        Product("전자기기", "노트북"),
        Product("반료동물용품", "건식사료"),
        Product("반료동물용품", "습식사료"),
        Product("반료동물용품", "치약"),
        Product("반료동물용품", "간식"),
    )

    private val categories: Map<String, List<Product>> = products.groupBy { products ->
        products.categoryLabel
    }

    fun showProducts(userSelectedCategory: String) {
        ScreenStack.push(this)
        val categoryProducts = categories[userSelectedCategory]

        if (!categoryProducts.isNullOrEmpty()) {
            println(
                """
                **==================================**
                선택하신 [$userSelectedCategory] 카테고리 상품입니다.
            """.trimIndent()
            )

            categoryProducts.forEachIndexed { index, product ->
                println("${index}. ${categoryProducts[index].name}")
            }

            showCartOption(categoryProducts, userSelectedCategory)

        } else {
            showEmptyProductMessage(userSelectedCategory)
        }
    }

    private fun showCartOption(categoriesProduct: List<Product>, userSelectedCategory: String) {
        println("""
           $LINE_DIVIDER
            장바구니에 담을 상품 번호를 선택해주세요.
        """.trimIndent()
        )

        val selectedIndex = readLine().getNotEmptyInt()
        categoriesProduct.getOrNull(selectedIndex)?.let { product ->
            CartItems.addProduct(product)
            println("=> 장바구니로 이동하시려면 #을, 계속 쇼핑하시려면 *을 입력해주세요.")
            val answer = readLine().getNotEmptyString()
            if (answer == "#") {
                val shoppingCart = ShoppingCart()
                shoppingCart.showCartItems()
            } else if (answer == "*") {
                showProducts(userSelectedCategory)
            } else {
                // TODO  그 외 값을 입력한 경우에 대한 처리
            }
        }
    }

    private fun showEmptyProductMessage(userSelectedCategory: String) {
        println("[${userSelectedCategory}] : 존재하지 않는 카테고리입니다. 다시 입력해주세요.")
    }
}