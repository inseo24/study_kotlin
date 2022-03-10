package screen

import data.Product

class ShoppingProductList {
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
        val categoryProducts = categories[userSelectedCategory]

        if (!categoryProducts.isNullOrEmpty()) {
            println(
                """
                **==================================**
                선택하신 [$userSelectedCategory] 카테고리 상품입니다.
            """.trimIndent()
            )
            val productSize = categoryProducts.size
            for (index in 0 until productSize) {
                println("${index}. ${categoryProducts[index].name}")
            }
        } else {
            showEmptyProductMessage(userSelectedCategory)
        }
    }

    private fun showEmptyProductMessage(userSelectedCategory: String) {
        println("[${userSelectedCategory}] : 존재하지 않는 카테고리입니다. 다시 입력해주세요.")
    }
}