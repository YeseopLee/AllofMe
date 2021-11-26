# AllofMe (포트폴리오 앱)

Firebase를 기반으로 한 게시물 올리기 앱

## 스택

AAC-ViewModel, LiveData, State Pattern\
Firebase Store & Storage (Repository Layer) & Auth\
Coroutines, Koin, View-Binding


## 동작

![AlertLogin](https://user-images.githubusercontent.com/67935576/143571608-a1b6cea3-b090-4170-841c-c26893de10cf.png)

![AlertField](https://user-images.githubusercontent.com/67935576/143571611-61b8966c-abc4-4dfe-88dc-90872052081d.png)

![Flow](https://user-images.githubusercontent.com/67935576/143563620-227b38ad-8220-4152-bdc0-60abb84fb091.gif)

![Flow2](https://user-images.githubusercontent.com/67935576/143571787-2324e17e-9a60-49b5-82a0-d86167722a70.gif)

## 구현

모바일에서 게시물을 포스팅할 때도 마치 PC에서 하듯이 텍스트와 이미지를 자유롭게 배치할 수 있게끔 만들어보려고 했다.\
RecyclerView를 활용하여, EditText와 ImageView를 동적으로 생성, 삭제 하여 해당 기능을 구현하였다.\
이 과정에서 반복되는 RecyclerView활용으로, adpater의 재사용 및 viewHolder 구분을 위해 enum class로 CellType을 정의하여 분기하여 활용하였다.\
State패턴을 적용하여 view에서 viewModel 로직의 State를 observing 하여 로직 상태에 따라 ui의 상태를 변화시킨다.


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
데이터 구조 및 코드를 크게 건드리지 않으면서 가장 간단하게 해결할 수 있는 방법이라고 생각하였다.
다른 방법으로는, 처음부터 one-way형식의 viewbinding을 사용하기보다는 two-way의 databinding을 사용하여 livedata에 직접 연결하는 방법도 생각해볼 수 있고,
또는 state.success에 담기는 model data를 view에서 직접 넣어주는방법도 가능하겠지만, view에서 state상태를 직접 바꾸는 방법은 그닥 좋지 못한것이라고 생각한다.



## 후기

RecyclerView를 동적으로 생성, 삭제하는 과정에서 data의 fetch를 계속 반복하다보니, ImageView를 삭제, 추가할 때 본문 전체가 새로이 fetch되어
사용자 경험이 떨어질 수 있어 보인다.

기획 단계에서 데이터 모델의 구조에 대하여 좀 더 깊은 고민을 하면 좋을듯. 프로젝트 규모가 커질수록 모델의 구조를 변경하는 경우가 빈번하게 일어났다.

EditText를 동적으로 계속 생성해야할 일이 있으면 ViewBinding 대신 DataBinding을 활용하는것이 더 깔끔하고 쉽게 설계할 수 있을것 같다.



