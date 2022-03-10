package screen

class ShoppingHome {

    fun startApp() {
        showGreetingMessage()
        showCategories()
    }

    fun showGreetingMessage() {
        println("hello! write your name please!")

        val name = readLine()
        println(
            """
        welcome! $name! 
        원하는 카테고리를 입력해주세요! :)
        ====================
        """.trimIndent()
        )
    }

    private fun showCategories() {
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
            // TODO 2. 카테고리 상품 목록 보여주기
            // TODO 3. 사용자가 카테고리 목록에 없는 값을 입력한 경우 처리
        }
    }
}