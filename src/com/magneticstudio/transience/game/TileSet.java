package com.magneticstudio.transience.game;

import com.magneticstudio.transience.ui.Game;
import com.magneticstudio.transience.ui.LogicalElement;
import com.magneticstudio.transience.util.ArrayList2D;
import com.magneticstudio.transience.util.Cache;
import com.magneticstudio.transience.util.FlowPosition;
import com.magneticstudio.transience.util.IntPoint;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.util.List;

/**
 * This class manages a 2d array of tiles.
 *
 * @author Max
 */
public class TileSet implements LogicalElement {

    /**
     * Loads/adds a new font to be used by a specific
     * tile-set object.
     * @param id The hashCode of the TileSet object.
     * @param ppt Pixels per tile.
     */
    private static void loadTileSetFont(int id, int ppt) throws SlickException {
        Cache.addFont(Cache.FONT_RESOURCE_FOLDER + "Consolas.ttf", Integer.toString(id), Font.PLAIN, ppt * 7 / 8);
    }

    /**
     * Gets the font for the specified tile-set hash.
     * @param id The hashCode of a tile set object.
     * @return The true type font used to render that particular tile set.
     */
    private static TrueTypeFont getTileSetFont(int id) {
        return Cache.getFont(Integer.toString(id));
    }

    /**
     * Gets the name of the font used by the tile set
     * with the specified id.
     * @param id The id of the tile set.
     * @return The name of the font used by the tile set.
     */
    private static String getTileSetFontName(int id) {
        return Integer.toString(id);
    }

    /**
     * Modifies the font size to adjust to the pixels per
     * tile of the tile set.
     * @param id The id of the tile set.
     * @param ppt The pixels per tile required by the tile set.
     */
    private static void modifyTileSetFont(int id, int ppt) {
        Cache.modifyFont(Integer.toString(id), Font.PLAIN, ppt * 7 / 8);
    }

    private ArrayList2D<Tile> tiles = new ArrayList2D<>(); // The 2d array of tiles.
    private FlowPosition position = new FlowPosition(); // The position of this tile set.

    private int pixelsPerTile = 32; // Amount of pixels (width and height) a tile takes up.
    private float finePixelOffsetX = 0; // The fine pixel offset horizontally.
    private float finePixelOffsetY = 0; // The fine pixel offset vertically.

     /**
     * Creates a new TileSet object with
     * the specified dimensions.
     * @param width The amount of tiles on the x axis.
     * @param height The amount of tiles on the y axis.
     */
    public TileSet(int width, int height) {
        tiles.setDimensions(width, height);
        try {
            loadTileSetFont(hashCode(), pixelsPerTile);
        }
        catch(SlickException ignore) {
            System.out.println("No Bueno");
            // Won't render the tile.
        }
        tiles.fill(new Tile(getTileSetFontName(hashCode())));
    }

    /**
     * Hides the tile at the specified location.
     * @param x The X value of the tile's position.
     * @param y The Y value of the tile's position.
     */
    public void hideTile(int x, int y) {
        if(tiles.getElement(x, y) != null)
            tiles.getElement(x, y).setVisible(false);
    }

    /**
     * Hides the tile at the specified location.
     * @param loc Location of the tile to hide.
     */
    public void hideTile(IntPoint loc) {
        if(tiles.getElement(loc.getX(), loc.getY()) != null)
            tiles.getElement(loc.getX(), loc.getY()).setVisible(false);
    }

    /**
     * Sets the specified tile to be shown.
     * @param x The X value of the tile's position.
     * @param y The Y value of the tile's position.
     */
    public void showTile(int x, int y) {
        if(tiles.getElement(x, y) != null)
            tiles.getElement(x, y).setVisible(true);
    }

    /**
     * Sets the specified tile to be shown.
     * @param loc Location of the tile to show.
     */
    public void showTile(IntPoint loc) {
        if(tiles.getElement(loc.getX(), loc.getY()) != null)
            tiles.getElement(loc.getX(), loc.getY()).setVisible(true);
    }

    /**
     * Gets the FlowPosition object that controls the
     * position of this TileSet on screen.
     * @return The FlowPosition object controlling position/animation of this TileSet.
     */
    public FlowPosition getPosition() {
        return position;
    }

    /**
     * Centers on the specified tile.
     * @param x The X value of the location of the tile.
     * @param y The Y value of the location of the tile.
     */
    public void centerOn(int x, int y) {
        position.setTargetX( -Game.activeGame.getResolutionWidth() / pixelsPerTile / 2 + x );
        position.setTargetY( -Game.activeGame.getResolutionHeight() / pixelsPerTile / 2 + y );
    }

    /**
     * Creates fine adjustments to perfectly
     * center each tile with its designated
     * square; location.
     */
    public void setPixelPerfect() {
        finePixelOffsetX = Game.activeGame.getResolutionWidth() % pixelsPerTile / 2;
        if (Game.activeGame.getResolutionWidth() / pixelsPerTile % 2 == 0)
            finePixelOffsetX -= (pixelsPerTile / 2);

        finePixelOffsetY = (Game.activeGame.getResolutionHeight() / 2) -
                ((Game.activeGame.getResolutionHeight() / 2 / pixelsPerTile) + 1) * pixelsPerTile - (pixelsPerTile / 2) + pixelsPerTile;
    }

    /**
     * Gets the amount of pixels (width and height)
     * each tile takes up.
     * @return Pixels per tile.
     */
    public int getPixelsPerTile() {
        return pixelsPerTile;
    }

    /**
     * Sets the amount of pixels (width and height)
     * each tile takes up.
     * @param pixelsPerTile Pixels per tile.
     */
    public void setPixelsPerTile(int pixelsPerTile) {
        this.pixelsPerTile = Math.min(Math.max(pixelsPerTile, 8), 128);
        tiles.forEach(e -> e.getRepresentation().setDimensions(this.pixelsPerTile, this.pixelsPerTile));

        modifyTileSetFont(hashCode(), this.pixelsPerTile);
    }

    /**
     * Sets the alpha for all representations
     * of all tiles in this tile set.
     * @param alpha The alpha value for all tiles.
     */
    public void setAlpha(float alpha) {
        tiles.forEach(e -> e.getRepresentation().setAlpha(alpha));
    }

    /**
     * Gets the 2d array of tiles.
     * @return The 2d array of tiles.
     */
    public ArrayList2D<Tile> getTiles() {
        return tiles;
    }

    /**
     * Renders the tile set onto the screen.
     * @param entities The entities to render onto the tileset.
     * @param graphics The graphics object used to render anything on the main screen.
     */
    public void render(List<Entity> entities, Graphics graphics) {
        TrueTypeFont font = getTileSetFont(hashCode());
        if(font == null)
            return;

        graphics.setFont(font);

        for(int iy = 0; iy < tiles.getHeight(); iy++) {
            if(!willTileBeVisibleY(iy))
                continue;
            XLoop:
            for(int ix = 0; ix < tiles.getWidth(); ix++) {
                if(!willTileBeVisibleX(ix))
                    continue;

                float renderX = (pixelsPerTile * ix) - (position.getIntermediateX() * pixelsPerTile) + finePixelOffsetX;
                float renderY = (pixelsPerTile * iy) - (position.getIntermediateY() * pixelsPerTile) + finePixelOffsetY;

                for(Entity e : entities) {
                    if(e.getPosition().isEquivalentTo(ix, iy)) {
                        e.render(
                            graphics,
                            renderX,
                            renderY,
                            false
                        );
                        continue XLoop;
                    }
                }

                tiles.getElement(ix, iy).render(
                    graphics,
                    renderX,
                    renderY,
                    false
                );
            }
        }
    }

    /**
     * Updates this tile set, so when the player moves within
     * the game, the tile set will center around the player.
     * @param milliseconds The elapsed time since last update.
     */
    @Override
    public void update(int milliseconds) {
        position.update(milliseconds);
    }

    /**
     * Checks if the specified column would be visible on the screen;
     * the tile itself isn't out of bounds of the rendering window.
     * @param x The column number.
     * @return Whether that column would be visible or not.
     */
    private boolean willTileBeVisibleX(int x) {
        return (x - position.getIntermediateX()) * pixelsPerTile + finePixelOffsetX >= -pixelsPerTile
                && (x - position.getIntermediateX()) * pixelsPerTile + finePixelOffsetX < Game.activeGame.getResolutionWidth();
    }

    /**
     * Checks if the specified row would be visible on the screen;
     * the tile itself isn't out of bounds of the rendering window.
     * @param y The row number.
     * @return Whether that row would be visible or not.
     */
    private boolean willTileBeVisibleY(int y) {
        return (y - position.getIntermediateY()) * pixelsPerTile + finePixelOffsetY >= -pixelsPerTile
                && (y - position.getIntermediateY()) * pixelsPerTile + finePixelOffsetY < Game.activeGame.getResolutionHeight();
    }
}
