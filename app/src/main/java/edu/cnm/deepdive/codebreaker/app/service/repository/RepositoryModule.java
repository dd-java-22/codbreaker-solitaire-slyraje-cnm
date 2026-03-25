package edu.cnm.deepdive.codebreaker.app.service.repository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import jakarta.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public interface RepositoryModule {

  @Binds
  @Singleton
  GameSummaryRepository bindGameSummaryRepository(GameSummaryRepositoryImpl implementation);

}
