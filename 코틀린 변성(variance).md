# kotlin variance 정리

- 코틀린 함수 타입의 모든 파라미터 타입은 contravariant(in), 모든 리턴 타입은 covariant(out)
- 리턴만 되는 타입에는 out, 허용만 되는 타입에는 in을 사용한다.
- 제네릭 타입을 인스턴스화할 때 서로 다른 타입 인자가 들어가는 경우, 인스턴스 타입 사이의 하위 타입 관계가 성립하지 않는 걸 무공변이라고 한다.(invariant)
- 타입 인자 사이에 하위 타입 관계가 성립하고, 그 하위 타입 관계가 그대로 인스턴스 타입 사이의 관계로 이어지면 공변이라고한다.(covariant)
- 왜 List를 MutableList 보다 더 많이 사용할까?
    - List의 타입 파라미터는 covariant(out 한정자) 입니다. 예를 들어, List<Any>가 예상되는 모든 곳에 전달할 수 있습니다.
    - MutableList의 타입 파라미터는 invariant 입니다.
    
    ```kotlin
    fun addInvalidValue(list: MutableList<Any>) {
    	list.add("text")
    }
    
    fun main() {
    	val nums = mutableListOf(1, 2, 3)
    	addInvalidValue(nums) // compile error
    }
    ```
    
    - 위와 같이 에러가 발생해서 list에 의도하지 않은 타입인자가 추가되는 걸 막을 수 있다. MutableList의 경우 무공변이기 때문에 MutableList<String>은 MutableList<Any>의 하위 타입이 아니다.
    
    ```kotlin
    fun printList(list: MutableList<Any>){
        println(list.joinToString())
    }
    
    fun main(){
        val numbers = mutableListOf(1,2,3) // MutableList<Int>
        printList(numbers) // compile error
    }
    ```
    
    - 위의 코드에서 printList 함수는 단순히 list 요소를 toString을 이어준 결과 값을 출력하고자 하는 것이기 때문에 굳이 MutableList<Int>가 필요하지 않다. MutableList<Any>이어도 list에 영향을 미치지 않고, 값을 오류 없이 읽어서 출력할 수 있다.
    - 하지만 이 경우에도 컴파일 오류가 발생한다. 따라서 이 둘의 관계를 정의해서 printList의 오류를 없애고 이 함수의 활용성을 확장할 수 있게 하는 것이 변성(covariant)이다.
    - 변성을 잘 활용하면 사용에 불편하지 않으면서 타입 안전성을 보장하는 API를 만들 수 있다.
    
    ```kotlin
    fun printList(list: List<Any>){
        println(list.joinToString())
    }
    
    fun main(){
        val numbers = listOf<Int>(1,2,3) // List<Int>
        printList(numbers) // error 없음
    }
    ```
    
- 자바의 배열이 covariant라서 발생하는 문제?
    - covariant인 이유는 배열을 기반으로 제네릭 연산자는 정렬 함수등을 만들기 위해서라고 얘기함
    - 문제는 컴파일 중이 아닌 런타임에 오류가 발생하는 경우가 있다.
    
    ```java
    Integer[] numbers = {1, 4, 2, 1};
    Object[] objects = numbers;
    objects[2] = "B"; // runtime error: ArrayStoreException
    ```
    
    - numbers를 Object[]로 캐스팅해도 구조 내부에서 사용되고 있는 실질적인 타입이 바뀌는 것이 아니다. 여전히 Integer이다. 따라서 이 배열에 문자열을 할당하면 당연히 오류가 발생한다.
    - 이런 결함을 고치기 위해 코틀린에서는 Array를 invariant로 만들어서 Array<Int>를 Array<Any>로 변경할 수 없게 했다.
- 레퍼런스
    [블로그](https://leejaeho.dev/posts/kotlin-generic-variance/)