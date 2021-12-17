# AllofMe (포트폴리오 앱)

Firebase를 기반으로 한 게시판형 앱

## 스택

AAC-ViewModel, LiveData, State Pattern\
Firebase Store & Storage (Repository Layer) & Auth\
Coroutines, Koin, View-Binding


## 동작

#### 글쓰기 및 수정, 삭제 동작 모습
![Flow](https://user-images.githubusercontent.com/67935576/143563620-227b38ad-8220-4152-bdc0-60abb84fb091.gif)![Flow3](https://user-images.githubusercontent.com/67935576/143681339-2e99428c-99aa-4fac-bd37-dbf93a62f281.gif)
#### loading shimmer
![shimmer](https://user-images.githubusercontent.com/67935576/146502383-db987a86-6b87-4271-a1e4-4628cd06e01e.gif)

![AlertField](https://user-images.githubusercontent.com/67935576/143571611-61b8966c-abc4-4dfe-88dc-90872052081d.png)![cantload](https://user-images.githubusercontent.com/67935576/143686593-7e425233-636c-4222-8375-a87d4262416d.png)


## 구현

모바일에서 게시물을 포스팅할 때도 마치 PC에서 하듯이 텍스트와 이미지를 자유롭게 배치할 수 있게끔 만들어보려고 했다.\
RecyclerView를 활용하여, EditText와 ImageView를 동적으로 생성, 삭제 하여 해당 기능을 구현하였다.\
이 과정에서 반복되는 RecyclerView활용으로, adpater의 재사용 및 viewHolder 구분을 위해 enum class로 CellType을 정의하여 분기하여 활용하였다.\
GOF State패턴을 적용하여 view에서 viewModel 로직의 State를 observing 하여 로직 상태에 따라 ui의 상태를 변화시킨다.\
게시물 클릭시 id를 조회하여 본인일 경우 수정 및 삭제 플로팅 버튼이 나타난다.\
추후 데이터가 많아졌을 때 로딩속도를 고려하여 loading shimmer를 추가하였다. 현재는 직관적으로 보여주기 위해 일부러 dealy를 걸어놓았다.


## 이슈

EditText가 동적으로 생성되는 과정에서 스크롤이 넘어가면 문자열이 소실되는 문제가 발생,\
TextWatcher를 만들어 해결하였다.
``` Kotlin
  override fun bindData(model: PostArticleModel) = with(binding) {
        binding.descriptionEditText.addTextChangedListener(MyTextWatcher(model))
        descriptionEditText.setText(model.text)
        super.bindData(model)

    }

    inner class MyTextWatcher(var model: PostArticleModel) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            model.text = s.toString()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }
```
\
ImageView가 추가되어 복수의 EditText의 값을 viewModel로 넘겨 줄 때, 제일 마지막의 EditText 값이 소실되는 문제가 발생.\
View에서 별도의 문자열리스트를 만들어 EditText값을 전부 담은 뒤 viewModel에 따로 전달, 꺼내서 사용하는식으로 해결하였다.
```Kotlin
val editTextList = descAdapter.currentList.filter { it.text != null }
viewModel.stringList.clear()
for (i in editTextList.indices) {
  viewModel.stringList.add(editTextList[i].text!!)
}
```
view-viewModel의 분리에 영향을 미치지 않고 코드를 크게 건드리지 않으면서 가장 간단하게 해결할 수 있는 방법이라고 생각하였다.\
다른 방법으로는, 처음부터 one-way형식의 viewbinding을 사용하기보다는 two-way의 databinding을 사용하여 livedata에 직접 연결하는 방법도 생각해볼 수 있고,\
또는 state.success에 담기는 model data를 view에서 직접 넣어주는방법도 가능하겠지만, view에서 state상태를 직접 바꾸는 방법은 그닥 좋지 못한것이라고 생각하였다.



## 후기

기획 단계에서 데이터 모델의 구조에 대하여 좀 더 깊은 고민을 하면 좋을듯. 프로젝트 규모가 커질수록 모델의 구조를 변경하는 경우가 빈번하게 일어났다.

EditText를 동적으로 계속 생성해야할 일이 있으면 ViewBinding 대신 DataBinding을 활용하는것이 더 깔끔하고 쉽게 설계할 수 있을것 같다.

이전에 토이프로젝트로 RESTful Api를 기반으로 커뮤니티형 앱 [LIFO](https://github.com/YeseopLee/LIFO)를 만든적이 있는데, 당시에는 아키텍쳐에 대한 설계없이 기능구현 후 -> MVVM 패턴으로 리팩토링하여 진행하였다. 그때는 오히려 관리해야할 파일들만 늘어나고 구조가 더 복잡해보이기만 해서 아키텍쳐 설계에 대해 큰 감흥이 없었는데, 아키텍쳐를 먼저 셋업하고 정해진 모듈에 따라 기능들을 이어붙여 나가니 개발 속도도 오르는것 같고, 코드를 전체적으로 훨씬 깔끔하게 개발할 수 있게 돼었다. 

대부분 개발을 혼자서 하다보니 깃허브를 단순히 저장소로만 사용하고 있었는데, 처음으로 issue와 projects의 칸반보드를 사용해보았다. 프로젝트가 진행되면서는 어느정도 기능별로 브랜치를 나누는 등 관리가 다소 쉬워졌지만, 초기 셋업단계에서는 브랜치를 확실하게 나누는것이 아직 좀 익숙하지 않다. 그래도 언제라도 다른 개발자와 같이 작업할 때를 대비하여 깃허브로 프로젝트를 관리하는 법에 계속 익숙해져야겠다.




