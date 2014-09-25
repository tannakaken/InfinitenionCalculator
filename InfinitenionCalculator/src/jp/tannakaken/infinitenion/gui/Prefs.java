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
 * �ݒ��ʁB
 * @author tannakaken
 *
 */
public class Prefs extends PreferenceActivity {
	
	/**
	 * �������[�h���ǂ����̃I�v�V�����B
	 */
	private static final String OPT_REAL = "real";
	/**
	 * �������[�h�̃f�t�H���g�l��false�B
	 */
	private static final boolean OPT_REAL_DEF = false;
	/**
	 * �������[�h�̐��x�B
	 */
	private static final String OPT_SCALE = "scale";
	/**
	 * �������x�̃f�t�H���g�l��7�B
	 */
	private static final String OPT_SCALE_DEF = "7";
	
	@Override
	protected final void onCreate(final Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	}
	/**
	 * ���ݗL�������[�h���ǂ����̔��肷�郁�\�b�h�B
	 * @param aContext ���肵�悤�Ƃ��Ă���Context�B
	 * @return ����Context�ɂ����ėL�������[�h���ǂ����B
	 */
	public static boolean getIsReal(final Context aContext) {
		return PreferenceManager.getDefaultSharedPreferences(aContext).getBoolean(OPT_REAL, OPT_REAL_DEF);
	}
	/**
	 * 
	 * @param aContext ���肵�悤�Ƃ��Ă���Context�B
	 * @return ����Context�ɂ����Ă̎����̐��x�B
	 */
	public static int getScale(final Context aContext) {
		return Integer.parseInt(
				PreferenceManager.getDefaultSharedPreferences(aContext).getString(OPT_SCALE, OPT_SCALE_DEF)
				);
	}
	/**
	 * �ݒ��ʂ��\������Fragment�B
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
				Toast.makeText(getActivity(), "�������[�h�ɓ���܂����B�ϐ��͑S�ăN���A����܂��B", Toast.LENGTH_SHORT).show();
			} else {
				VariableFactory.getInstance().clearVariable();
				Toast.makeText(getActivity(), "�L�������[�h�ɓ���܂����B�ϐ��͑S�ăN���A����܂��B", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
	}
}
