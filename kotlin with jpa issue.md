### Issue

1. N+1 문제
    
    JPA는 메서드 이름을 분석해 JPQL을 생성해 실행한다. JPQL은  SQL을 추상화한 객체지향 쿼리 언어로 특정 SQL에 종속되지 않고 엔티티 객체와 필드 이름을 갖고 쿼리를 한다.
    
    그렇기 때문에 findAll()이란 메서드를 수행했을 때 해당 엔티티를 조회하는 `select * from table` 쿼리만 실행하게 된다. 
    
    JPQL 입장에서는 연관관계 데이터를 무시하고 해당 엔티티를 기준으로 쿼리를 조회하기 때문이다.
    
    그렇기 때문에 엔티티 데이터가 필요한 경우, FetchType으로 지정한 시점에 조회를 별도로 호출하게 된다.
    
    즉, 하위 엔티티들을 첫 쿼리 실행 시 한 번에 가져오는게 아니라, Lazy Loading으로 필요한 곳에서 사용되어 쿼리가 실행될 때 발생하는 문제
    
    예를 들어, 회사 1개와 직원 10명이 있다면 회사 1개 조회시 10개의 추가 쿼리가 발생하게 됨
    
    FetchType.LAZY로 변경했다고 해서 문제가 꼭 없어지는 건 아닌듯
    
    → 연관 엔티티 참조 하면 추가적인 쿼리가 수행됨
    

### FetchType.EAGER

@ManyToOne의 기본 FetchType은 EAGER이기 때문에 관계에 필요한 데이터를 바로 조회한다. 

만약 direct fetching 조회하면 hibernate는 left join으로 쿼리를 생성해 조회한다.

inner join으로 변경하고 싶다면 @ManyToOne의 optional을 false로 변경하면 된다. 기본값은 true

`@ManyToOne(optional = false)`

해결하는 방법은 여러 개가 있다.

1) Fetch join을 @Query로 작성하기

```kotlin
@Query("select a from company a join fetch a.person")
List<Company> findAllJoinFecth();
```

단, 이렇게 하면 FetchType.LAZY가 작동하지 않고 페이징 쿼리를 쓸 수 가 없다. 하나의 쿼리문으로 가져오다 보니 페이징 단위로 데이터를 가져오는게 불가능함

또한 둘 이상의 컬렉션을 페치할 수 없음

2) @EntityGraph

attributePaths에 쿼리 수행 시 바로 가져올 필드명을 지정하면 Lazy가 아닌 Eager 조회로 가져오게 된다. Fetch join과 마찬가지로 JPQL을 사용하여 쿼리문을 작성하고 필요한 연관관계는 @EntityGraph에 설정하면 된다. 

```kotlin
@EntityGraph(attributePaths = "person")
@Query("select a from Company a")
List<Company> findAllEntityGraph();
// 위와 같이 필드명을 지정하면, Eager 조회로 가져온다. 
// 원본 쿼리의 손상 없이 정의하고 사용할 수 있음
```

1번과 2번 모두 주의할 점은 Join문으로 호출하다 보니 **카테시안 곱**이 발생해 중복 데이터가 존재할 수 있다는 점이다. 

→ 1) 중복 제거를 위해 Set을 쓰거나,

→ 2) JPQL에서 distinct를 사용해 중복 데이터를 조회하지 않거나

3) @NamedEntityGraphs

Entity에 관련해 모든 설정 코드를 추가해야함

4) FetchMode.SUBSELECT

이 해결 방법은 2번의 쿼리로 해결한다. 

엔티티를 조회하는 쿼리는 그대로 발생하고, 연관관계의 데이터를 조회할 때 서브 쿼리로 함께 조회하는 방법이다.

즉시로딩으로 설정하면 조회시점에, 지연로딩으로 설정하면 지연로딩된 엔티티를 사용하는 시점에 위의 쿼리가 실행된다. 모두 지연로딩으로 설정하고 성능 최적화가 필요한 곳에는 JPQL 페치 조인을 사용하는 것이 추천되는 전략이다.

5) BatchSize

하이버네이트가 제공하는 `org.hibernate.annotations.BatchSize` 어노테이션을 이용하면 연관 엔티티를 조회할 때 지정된 size 만큼 SQL의 IN절을 사용해 조회한다.

`@BatchSize(size = 5)`

> hibernate.default_batch_fetch_size 속성을 사용하면 애플리케이션 전체에 기본으로 @BatchSize를 적용할 수 있다.
> 

6) QueryBuilder를 사용해보기

Query를 실행하도록 지원해주는 다양한 플러그인이 있다. 대표적으로 Mybatis, QueryDSL, JOOQ, JDBC Template 등이 있다. 이걸 사용해 로직에 최적화된 쿼리를 구현할 수 있다.

글쓴이가 가장 추천하는 건 QueryBuilder를 함께 사용하는 것 같다. 생각보다 다양한 이슈를 큰 고민 없이 해결할 수 있다고 함!