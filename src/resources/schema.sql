CREATE TABLE sites (
                       id INTEGER PRIMARY KEY,
                       name TEXT NOT NULL UNIQUE,
                       url TEXT
);

CREATE TABLE problems (
                          id INTEGER PRIMARY KEY,
                          site_id INTEGER NOT NULL,
                          code TEXT NOT NULL,
                          title TEXT,
                          difficulty TEXT,
                          link TEXT,
                          tries INTEGER,
                          FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE
);

CREATE TABLE notes (
                       id INTEGER PRIMARY KEY,
                       problem_id INTEGER NOT NULL,
                       content TEXT NOT NULL,
                       date_added TEXT DEFAULT CURRENT_TIMESTAMP,
                       last_updated TEXT,
                       FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

CREATE INDEX idx_problems_site_id ON problems(site_id);
CREATE INDEX idx_notes_problem_id ON notes(problem_id);


