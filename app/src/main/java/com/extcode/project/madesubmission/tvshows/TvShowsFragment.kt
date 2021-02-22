package com.extcode.project.madesubmission.tvshows

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.extcode.project.core.data.Resource
import com.extcode.project.core.domain.model.Movie
import com.extcode.project.core.ui.MoviesAdapter
import com.extcode.project.core.utils.SortUtils
import com.extcode.project.madesubmission.R
import com.extcode.project.madesubmission.databinding.FragmentTvShowsBinding
import com.extcode.project.madesubmission.detail.DetailActivity
import com.extcode.project.madesubmission.utils.DataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@FlowPreview
class TvShowsFragment : Fragment(){

    private var fragmentTvShowsBinding: FragmentTvShowsBinding? = null
    private val binding get() = fragmentTvShowsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentTvShowsBinding = FragmentTvShowsBinding.inflate(inflater, container, false)
        val toolbar: Toolbar = activity?.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        return binding?.root
    }

    private val viewModel: TvShowsViewModel by viewModel()
    private lateinit var tvShowsAdapter: MoviesAdapter
    private var sort = SortUtils.RANDOM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvShowsAdapter = MoviesAdapter()
        setList(sort)

        with(binding?.rvTvShows) {
            this?.layoutManager = GridLayoutManager(context, 2)
            this?.setHasFixedSize(true)
            this?.adapter = tvShowsAdapter
        }

        tvShowsAdapter.onItemClick = { selectedData ->
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_MOVIE, selectedData)
            startActivity(intent)
        }
    }

    private fun setList(sort: String) {
        viewModel.getTvShows(sort).observe(viewLifecycleOwner, tvShowsObserver)
    }

    private val tvShowsObserver = Observer<Resource<List<Movie>>> { tvShow ->
        if (tvShow != null) {
            when (tvShow) {
                is Resource.Loading -> setDataState(DataState.LOADING)
                is Resource.Success -> {
                    setDataState(DataState.SUCCESS)
                    tvShowsAdapter.setData(tvShow.data)
                }
                is Resource.Error -> {
                    setDataState(DataState.ERROR)
                    Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setDataState(state: DataState) {
        when (state) {
            DataState.ERROR -> {
                binding?.progressBar?.visibility = View.GONE
                binding?.notFound?.visibility = View.VISIBLE
                binding?.notFoundText?.visibility = View.VISIBLE
            }
            DataState.LOADING -> {
                binding?.progressBar?.visibility = View.VISIBLE
                binding?.notFound?.visibility = View.GONE
                binding?.notFoundText?.visibility = View.GONE
            }
            DataState.SUCCESS -> {
                binding?.progressBar?.visibility = View.GONE
                binding?.notFound?.visibility = View.GONE
                binding?.notFoundText?.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.rvTvShows?.adapter = null
        fragmentTvShowsBinding = null
    }
}
