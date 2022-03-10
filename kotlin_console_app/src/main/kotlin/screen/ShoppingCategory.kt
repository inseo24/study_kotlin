package screen

class ShoppingCategory {

    fun showCategories() {
        val categories = arrayOf("패션", "전자기기", "반려동물용품")
        for (category in categories) {
            println(category)
        }

        println("=> 장바구니로 이동하려면 #을 입력해주세요.")

        var userSelectedCategory = readLine()
        while (userSelectedCategory.isNullOrBlank()) {
            println("값을 입력해주세요")
            userSelectedCategory = readLine()
        }

        if (userSelectedCategory == "#") {
            // TODO 1. 장바구니 이동
        } else {
            if (categories.contains(userSelectedCategory)) {
                // TODO 2. 카테고리 상품 목록 보여주기

            } else {
                println("[${userSelectedCategory}] : 존재하지 않는 카테고리입니다. 다시 입력해주세요.")
                showCategories()
            }
        }
    }
}