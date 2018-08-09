/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.itomaszjanik.notepadsync;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;

/**
 * An activity to illustrate how to delete a file.
 */
public class DeleteFileActivity extends BaseDemoActivity {
    private static final String TAG = "DeleteFileActivity";
    private static String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            title = bundle.getString("title", "");
        }
    }

    @Override
    protected void onDriveClientReady() {
        getDriveClient().requestSync();
        queryFolder();
    }

    private Query setQueryFolder(){
        // [START query_title]
        return new Query.Builder()
                .addFilter(Filters.and(
                        Filters.eq(SearchableField.MIME_TYPE, "application/vnd.google-apps.folder"),
                        Filters.eq(SearchableField.TITLE, "NotepadSync"),
                        Filters.eq(SearchableField.TRASHED, false)
                ))
                .build();
        // [END query_title]
    }

    private void queryFolder(){
        final Query query = setQueryFolder();

        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query)
                .addOnSuccessListener(this, buffer -> {
                    if (buffer != null && buffer.getCount() > 0){
                        queryFile(buffer.get(0).getDriveId());
                    }
                    else{
                        Log.e(TAG, "Error retrieving folder");
                        finish();
                    }
                })
                .addOnFailureListener(this, buffer ->{
                    Log.e(TAG, "Error retrieving folder");
                    finish();
                });
    }

    private Query setQueryFile(String title, DriveId driveId){
        // [START query_title]
        return new Query.Builder()
                .addFilter(Filters.and(
                        Filters.eq(SearchableField.MIME_TYPE, "text/plain"),
                        Filters.in(SearchableField.PARENTS, driveId),
                        Filters.eq(SearchableField.TITLE, title + ".txt"),
                        Filters.eq(SearchableField.TRASHED, false)
                ))
                .build();
        // [END query_title]
    }

    private void queryFile(DriveId driveId){
        if (driveId == null){
            Log.e(TAG, "Error retrieving folder");
            return;
        }

        final Query query = setQueryFile(title, driveId);

        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query)
                .addOnSuccessListener(this, buffer -> {
                    if (buffer != null && buffer.getCount() > 0){
                        deleteFile(buffer.get(0).getDriveId().asDriveFile());
                    }
                    else{
                        Log.e(TAG, "Error retrieving file");
                        finish();
                    }
                })
                .addOnFailureListener(this, buffer ->{
                    Log.e(TAG, "Error retrieving file");
                    finish();
                });
    }

    private void deleteFile(DriveFile driveFile) {
        if (driveFile == null){
            Log.e(TAG, "Error retrieving file");
            return;
        }

        // [START delete_file]
        getDriveResourceClient()
                .delete(driveFile)
                .addOnSuccessListener(this,
                        aVoid -> finish()
                )
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to delete file", e);
                    finish();
                });
        // [END delete_file]

    }
}
