package ru.medbox.ui.handler

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.medbox.db.model.Article
import ru.medbox.db.model.Category
import ru.medbox.db.model.Lecture
import ru.medbox.ui.activity.*
import ru.medbox.utils.ARTICLE_KEY
import ru.medbox.utils.CATEGORY_KEY
import ru.medbox.utils.LECTURE_KEY

class SalutaryEventHandler(val context: Context?) {

    fun onCategoryClick(categoryArticle: Category) {
        if (context is MainActivity) {
            val bundle = Bundle()
            bundle.putInt(CATEGORY_KEY, categoryArticle.id)
            val intent = Intent(context, ArticlesActivity().javaClass)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    fun onArticleClick(article: Article) {
        if (context is ArticlesActivity) {
            val bundle = Bundle()
            bundle.putInt(CATEGORY_KEY, article.categoryId)
            bundle.putInt(ARTICLE_KEY, article.id)
            val intent = Intent(context, ArticleDetailActivity().javaClass)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    fun onLectureClick() {
        if (context is MainActivity) {
            val bundle = Bundle()
            val intent = Intent(context, LecturesActivity().javaClass)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    fun onLectureItemClick(lecture: Lecture) {
        if (context is LecturesActivity) {
            val bundle = Bundle()
            bundle.putInt(LECTURE_KEY, lecture.id)
            val intent = Intent(context, LectureDetailActivity().javaClass)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

}