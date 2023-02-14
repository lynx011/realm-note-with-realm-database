package com.example.realm_test.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realm_test.model.ArticleModel
import io.realm.Realm
import io.realm.kotlin.deleteFromRealm
import java.util.*

class RealmViewModel : ViewModel() {
    private val realm : Realm = Realm.getDefaultInstance()
    var realmLiveData = MutableLiveData<List<ArticleModel>>()

    fun addArticle(title: String, desc: String){
        realm.executeTransaction {
            val article = it.createObject(ArticleModel::class.java,UUID.randomUUID().toString())
            article.title = title
            article.description = desc
            realm.insertOrUpdate(article)
        }
    }

    fun updateArticle(id: String, title: String, desc: String){
        realm.executeTransaction {
            val article = it.where(ArticleModel::class.java).equalTo("id",id).findFirst()
            article?.title = title
            article?.description = desc
            if (article != null) {
                realm.insertOrUpdate(article)
            }
        }
    }

    fun deleteArticle(id: String){
        realm.executeTransaction {
            val article = it.where(ArticleModel::class.java).equalTo("id",id).findFirst()
            article?.deleteFromRealm()
        }
    }

    fun getArticle(){
        val article = realm.where(ArticleModel::class.java).findAll()
        realmLiveData.value = realm.copyFromRealm(article)
    }
}