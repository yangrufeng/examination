package org.chm.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.chm.base.BaseFragment;
import org.chm.bean.Answer;
import org.chm.dummy.DummyData;
import org.chm.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RadioBoxFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RadioBoxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RadioBoxFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final int A = 1;
    private final int B = 2;
    private final int C = 3;
    private final int D = 4;

    private OnFragmentInteractionListener mListener;

    public RadioBoxFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RadioBoxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RadioBoxFragment newInstance(String param1, String param2) {
        RadioBoxFragment fragment = new RadioBoxFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mListener = (OnFragmentInteractionListener)this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_radio_box, container, false);
        Answer a = DummyData.getAnswerById(mParam1);
        RadioButton a1 = (RadioButton)view.findViewById(R.id.answer1);
        RadioButton a2 = (RadioButton)view.findViewById(R.id.answer2);
        RadioButton a3 = (RadioButton)view.findViewById(R.id.answer3);
        RadioButton a4 = (RadioButton)view.findViewById(R.id.answer4);
        if (null != a.getAns()) {
            switch (Integer.parseInt(a.getAns())) {
                case A:
                    a1.setChecked(true);
                    break;
                case B:
                    a2.setChecked(true);
                    break;
                case C:
                    a3.setChecked(true);
                    break;
                case D:
                    a4.setChecked(true);
                    break;
                default:
                    break;

            }
        }

        a1.setText(a.getAns1());
        a1.setTag(R.id.tag_answer_num, 1);

        a2.setText(a.getAns2());
        a2.setTag(R.id.tag_answer_num, 2);

        a3.setText(a.getAns3());
        a3.setTag(R.id.tag_answer_num, 3);

        a4.setText(a.getAns4());
        a4.setTag(R.id.tag_answer_num, 4);

        RadioGroup clientRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        clientRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)group.findViewById(radioButtonId);
                Uri uri = Uri.parse("content://org.chm.examination/answer");
                Uri resultUri = ContentUris.withAppendedId(uri, Integer.parseInt(String.valueOf(rb.getTag())));
                RadioBoxFragment.this.onButtonPressed(resultUri);
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
