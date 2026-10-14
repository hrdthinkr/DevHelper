package io.github.hrdthinkr.devhelper.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.hrdthinkr.devhelper.domain.model.DevTool
import io.github.hrdthinkr.devhelper.ui.theme.Accent
import io.github.hrdthinkr.devhelper.ui.theme.AppBackground
import io.github.hrdthinkr.devhelper.ui.theme.CardBackground
import io.github.hrdthinkr.devhelper.ui.theme.SecondaryText

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredTools = uiState.tools.filter { tool ->
        tool.name.contains(uiState.searchQuery, ignoreCase = true) ||
                tool.description.contains(uiState.searchQuery, ignoreCase = true) ||
                tool.category.title.contains(uiState.searchQuery, ignoreCase = true)
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            background = AppBackground,
            surface = CardBackground,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Scaffold(
            containerColor = AppBackground
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 28.dp,
                    end = 20.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Header()
                }

                item {
                    SearchField(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange
                    )
                }

                item {
                    SectionHeader(
                        count = filteredTools.size
                    )
                }

                if (filteredTools.isEmpty()) {
                    item {
                        EmptySearchState()
                    }
                } else {
                    items(
                        items = filteredTools,
                        key = { it.id }
                    ) { tool ->
                        ToolCard(tool = tool)
                    }
                }

                item {
                    Footer()
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Accent,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "D",
                    color = AppBackground,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Column {
                Text(
                    text = "DevPocket",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "DEVELOPER TOOLKIT",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText,
                    letterSpacing = 1.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Build smarter.",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-1).sp
        )

        Text(
            text = "Your developer toolkit",
            fontSize = 16.sp,
            color = SecondaryText
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("Search developer tools...")
        },
        leadingIcon = {
            Text(
                text = "⌕",
                fontSize = 28.sp,
                color = SecondaryText
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent,
            unfocusedBorderColor = Color(0xFF30323D),
            focusedContainerColor = CardBackground,
            unfocusedContainerColor = CardBackground,
            cursorColor = Accent
        )
    )
}

@Composable
private fun SectionHeader(
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Your toolbox",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "$count tools available",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }

        Surface(
            color = Color(0xFF292536),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "DEV",
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 7.dp
                ),
                color = Accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ToolCard(
    tool: DevTool
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        color = Color(0xFF302942),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tool.icon,
                    fontSize = 20.sp,
                    color = Accent,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = tool.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                Text(
                    text = tool.description,
                    color = SecondaryText,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = tool.category.title,
                    color = Accent,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "›",
                fontSize = 30.sp,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "No tools found",
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Try another search",
            color = SecondaryText
        )
    }
}

@Composable
private fun Footer() {
    Text(
        text = "MADE FOR DEVELOPERS • DEVPOCKET",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        color = SecondaryText,
        fontSize = 10.sp,
        letterSpacing = 1.sp
    )
}