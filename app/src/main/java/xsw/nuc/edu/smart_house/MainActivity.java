package xsw.nuc.edu.smart_house;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import xsw.nuc.edu.smart_house.Adapter.MyFragmentAdapter;


public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private Fragment[] fragments = {new InfoFragment(), new ControlFragment()};
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();//初始化控件的办法
    }

    private void initView() {
        fragmentManager = getSupportFragmentManager();
        MyFragmentAdapter myFragmentAdapter = new MyFragmentAdapter(fragmentManager, fragments);
        viewPager = (ViewPager) findViewById(R.id.vp);
        viewPager.setAdapter(myFragmentAdapter);
    }
}
