package icu.nullptr.hidemyapplist.ui.activity

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import com.drakeet.about.AbsAboutActivity
import com.drakeet.about.Card
import com.drakeet.about.Category
import com.drakeet.about.Contributor
import com.drakeet.about.License
import com.drakeet.about.Line
import org.frknkrc44.hma_oss.BuildConfig
import org.frknkrc44.hma_oss.R

@Suppress("deprecation")
class AboutActivity : AbsAboutActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        icon.setImageResource(R.mipmap.ic_launcher)
        slogan.text = applicationInfo.loadLabel(packageManager)
        version.text = BuildConfig.VERSION_NAME
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.add(Category(getString(R.string.title_about_fork)))
        items.add(Card(getString(R.string.about_fork_description)))

        items.add(Category(getString(R.string.title_about)))
        items.add(Card(getString(R.string.about_description)))

        items.add(Category(getString(R.string.about_how_to_use_title)))
        items.add(Card(getString(R.string.about_how_to_use_description_1)))
        items.add(Line())
        items.add(Card(getString(R.string.about_how_to_use_description_2)))

        items.add(Category(getString(R.string.about_developer)))
        items.add(Contributor(R.drawable.cont_author, "\uD835\uDD93\uD835\uDD9A\uD835\uDD91\uD835\uDD91\uD835\uDD95\uD835\uDD99\uD835\uDD97", "Developer", "https://github.com/Dr-TSNG"))
        items.add(Line())
        items.add(Contributor(R.drawable.cont_fk, "frknkrc44", "HMA-OSS Developer", "https://github.com/frknkrc44"))
        items.add(Line())
        items.add(Contributor(R.drawable.cont_sk, "Shirkneko", "HMA-OSS Fork", "https://github.com/ShirkNeko"))
        items.add(Line())
        items.add(Contributor(R.drawable.cont_k, "Ketal", "Collaborator", "https://github.com/keta1"))
        items.add(Line())
        items.add(Contributor(R.drawable.cont_aviraxp, "aviraxp", "Collaborator", "https://github.com/aviraxp"))
        items.add(Line())
        items.add(Contributor(R.drawable.cont_icon_designer, "辉少菌", "Icon designer", "http://www.coolapk.com/u/1560270"))
        items.add(Line())
        items.add(Contributor(R.drawable.cont_cpp_master, "LoveSy", "Idea provider", "https://github.com/yujincheng08"))

        items.add(Category(getString(R.string.about_support)))
        items.add(Card("Github\nhttps://github.com/ShirkNeko/HMA-OSS"))
        items.add(Line())
        items.add(Card("Telegram\nhttps://t.me/ShirkNeko"))

        items.add(Category(getString(R.string.about_open_source)))
        items.add(License("MultiType", "drakeet", License.APACHE_2, "https://github.com/drakeet/MultiType"))
        items.add(License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"))
        items.add(License("EzXHelper", "KyuubiRan", License.APACHE_2, "https://github.com/KyuubiRan/EzXHelper"))
        items.add(License("libsu", "topjohnwu", License.APACHE_2, "https://github.com/topjohnwu/libsu"))
        items.add(License("okhttp", "square", License.APACHE_2, "https://github.com/square/okhttp"))
        items.add(License("rxhttp", "liujingxing", License.APACHE_2, "https://github.com/liujingxing/rxhttp"))
    }
}
