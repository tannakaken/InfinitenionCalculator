package jp.tannakaken.infinitenion.gui;

import jp.tannakaken.infinitenion.operand.VariableFactory;
import jp.tannakaken.infinitenion.R;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;
/**
 * 設定画面。
 * @author tannakaken
 *
 */
public class Prefs extends PreferenceActivity {
	
	/**
	 * 実数モードかどうかのオプション。
	 */
	private static final String OPT_REAL = "real";
	/**
	 * 実数モードのデフォルト値はfalse。
	 */
	private static final boolean OPT_REAL_DEF = false;
	/**
	 * 実数モードの精度。
	 */
	private static final String OPT_SCALE = "scale";
	/**
	 * 実数精度のデフォルト値は7。
	 */
	private static final String OPT_SCALE_DEF = "7";
	
	@Override
	protected final void onCreate(final Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	}
	/**
	 * 現在有理数モードかどうかの判定するメソッド。
	 * @param aContext 判定しようとしているContext。
	 * @return そのContextにおいて有理数モードかどうか。
	 */
	public static boolean getIsReal(final Context aContext) {
		return PreferenceManager.getDefaultSharedPreferences(aContext).getBoolean(OPT_REAL, OPT_REAL_DEF);
	}
	/**
	 * 
	 * @param aContext 判定しようとしているContext。
	 * @return そのContextにおいての実数の精度。
	 */
	public static int getScale(final Context aContext) {
		return Integer.parseInt(
				PreferenceManager.getDefaultSharedPreferences(aContext).getString(OPT_SCALE, OPT_SCALE_DEF)
				);
	}
	/**
	 * 設定画面を構成するFragment。
	 * @author tannakaken
	 *
	 */
	public static class PrefsFragment extends PreferenceFragment implements OnPreferenceChangeListener {	
		@Override
		public final void onCreate(final Bundle aSavedInstanceState) {
			super.onCreate(aSavedInstanceState);
			addPreferencesFromResource(R.xml.settings);
			
			CheckBoxPreference tCheckBox = (CheckBoxPreference) findPreference(getText(R.string.real_key));
			tCheckBox.setOnPreferenceChangeListener(this);
		}

		@Override
		public final boolean onPreferenceChange(final Preference aPreference, final Object aNewValue) {
			if (((Boolean) aNewValue).booleanValue()) {
				VariableFactory.getInstance().clearVariable();
				Toast.makeText(getActivity(), "実数モードに入りました。変数は全てクリアされます。", Toast.LENGTH_SHORT).show();
			} else {
				VariableFactory.getInstance().clearVariable();
				Toast.makeText(getActivity(), "有理数モードに入りました。変数は全てクリアされます。", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
	}
}
